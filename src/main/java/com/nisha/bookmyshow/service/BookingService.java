package com.nisha.bookmyshow.service;

import com.nisha.bookmyshow.dto.booking.BookingRequest;
import com.nisha.bookmyshow.dto.booking.BookingResponse;
import com.nisha.bookmyshow.dto.booking.BookingStatsResponse;
import com.nisha.bookmyshow.entity.Booking;
import com.nisha.bookmyshow.entity.BookingSeat;
import com.nisha.bookmyshow.entity.BookingStatus;
import com.nisha.bookmyshow.entity.PaymentStatus;
import com.nisha.bookmyshow.entity.Role;
import com.nisha.bookmyshow.entity.Show;
import com.nisha.bookmyshow.entity.ShowSeat;
import com.nisha.bookmyshow.entity.ShowSeatStatus;
import com.nisha.bookmyshow.entity.User;
import com.nisha.bookmyshow.exception.InvalidBookingException;
import com.nisha.bookmyshow.exception.ResourceNotFoundException;
import com.nisha.bookmyshow.exception.SeatNotAvailableException;
import com.nisha.bookmyshow.exception.UnauthorizedException;
import com.nisha.bookmyshow.repository.BookingRepository;
import com.nisha.bookmyshow.repository.ShowSeatRepository;
import com.nisha.bookmyshow.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ShowSeatRepository showSeatRepository;
    private final ShowService showService;
    private final SecurityUtil securityUtil;
    private final PaymentService paymentService;
    private final EmailService emailService;

    @Transactional
    public BookingResponse create(BookingRequest request) {
        User user = securityUtil.currentUser();
        Show show = showService.findDetailed(request.showId());

        List<Long> requestedIds = request.showSeatIds().stream().distinct().toList();
        if (requestedIds.isEmpty()) {
            throw new InvalidBookingException("Select at least one seat");
        }

        List<ShowSeat> locked = showSeatRepository.lockSeatsForShow(requestedIds, show.getId());
        if (locked.size() != requestedIds.size()) {
            throw new InvalidBookingException("One or more seats do not belong to this show");
        }

        for (ShowSeat seat : locked) {
            if (seat.getStatus() != ShowSeatStatus.AVAILABLE) {
                throw new SeatNotAvailableException("Seat " + seat.getSeat().getSeatNumber() + " is no longer available.");
            }
        }

        BigDecimal total = locked.stream()
                .map(ShowSeat::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String reference = "BMS" + System.currentTimeMillis()
                + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        PaymentStatus paymentStatus = paymentService.charge(reference, total);
        if (paymentStatus != PaymentStatus.SUCCESS) {
            throw new InvalidBookingException("Payment failed. Seats were not booked.");
        }

        locked.forEach(seat -> seat.setStatus(ShowSeatStatus.BOOKED));
        showSeatRepository.saveAll(locked);

        Booking booking = Booking.builder()
                .bookingReference(reference)
                .user(user)
                .show(show)
                .totalAmount(total)
                .bookingStatus(BookingStatus.CONFIRMED)
                .paymentStatus(PaymentStatus.SUCCESS)
                .build();

        for (ShowSeat showSeat : locked) {
            booking.getSeats().add(BookingSeat.builder()
                    .booking(booking)
                    .showSeat(showSeat)
                    .seatNumber(showSeat.getSeat().getSeatNumber())
                    .build());
        }

        Booking saved = bookingRepository.save(booking);
        BookingResponse response = toResponse(saved.getId());
        String email = user.getEmail();
        afterCommit(() -> emailService.sendBookingConfirmation(email, response));
        return response;
    }

    public Page<BookingResponse> myBookings(Pageable pageable) {
        User user = securityUtil.currentUser();
        return mapPage(bookingRepository.findByUserId(user.getId(), pageable));
    }

    public Page<BookingResponse> upcoming(Pageable pageable) {
        User user = securityUtil.currentUser();
        return mapPage(bookingRepository.findUpcoming(user.getId(), BookingStatus.CONFIRMED, LocalDate.now(), pageable));
    }

    public Page<BookingResponse> past(Pageable pageable) {
        User user = securityUtil.currentUser();
        return mapPage(bookingRepository.findPast(user.getId(), LocalDate.now(), pageable));
    }

    public BookingResponse get(Long id) {
        User user = securityUtil.currentUser();
        Booking booking = bookingRepository.findDetailedById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
        assertOwnerOrAdmin(user, booking);
        return BookingResponse.from(booking);
    }

    @Transactional
    public BookingResponse cancel(Long id) {
        User user = securityUtil.currentUser();
        Booking booking = bookingRepository.findDetailedById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
        assertOwnerOrAdmin(user, booking);

        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new InvalidBookingException("Only confirmed bookings can be cancelled");
        }

        LocalDateTime showStart = LocalDateTime.of(booking.getShow().getShowDate(), booking.getShow().getStartTime());
        if (!showStart.isAfter(LocalDateTime.now())) {
            throw new InvalidBookingException("Cannot cancel a show that has already started");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        for (BookingSeat bookingSeat : booking.getSeats()) {
            ShowSeat showSeat = bookingSeat.getShowSeat();
            showSeat.setStatus(ShowSeatStatus.AVAILABLE);
        }
        bookingRepository.save(booking);

        BookingResponse response = toResponse(booking.getId());
        String email = booking.getUser().getEmail();
        afterCommit(() -> emailService.sendBookingCancellation(email, response));
        return response;
    }

    public Page<BookingResponse> adminList(Pageable pageable) {
        return mapPage(bookingRepository.findAll(pageable));
    }

    public BookingStatsResponse stats() {
        return new BookingStatsResponse(
                bookingRepository.count(),
                bookingRepository.countByBookingStatus(BookingStatus.CONFIRMED),
                bookingRepository.countByBookingStatus(BookingStatus.CANCELLED),
                bookingRepository.countByBookingStatus(BookingStatus.PENDING)
        );
    }

    private Page<BookingResponse> mapPage(Page<Booking> page) {
        List<Long> ids = page.getContent().stream().map(Booking::getId).toList();
        if (ids.isEmpty()) {
            return Page.empty(page.getPageable());
        }
        Map<Long, Booking> detailed = bookingRepository.findDetailedByIdIn(ids).stream()
                .collect(Collectors.toMap(Booking::getId, Function.identity()));
        List<BookingResponse> content = ids.stream()
                .map(id -> BookingResponse.from(detailed.get(id)))
                .toList();
        return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
    }

    private BookingResponse toResponse(Long id) {
        return BookingResponse.from(bookingRepository.findDetailedById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id)));
    }

    private void assertOwnerOrAdmin(User user, Booking booking) {
        if (user.getRole() != Role.ADMIN && !booking.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only access your own bookings");
        }
    }

    private void afterCommit(Runnable action) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            action.run();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                action.run();
            }
        });
    }
}
