package com.nisha.bookmyshow.service;

import com.nisha.bookmyshow.dto.booking.BookingRequest;
import com.nisha.bookmyshow.entity.Movie;
import com.nisha.bookmyshow.entity.Screen;
import com.nisha.bookmyshow.entity.Seat;
import com.nisha.bookmyshow.entity.SeatType;
import com.nisha.bookmyshow.entity.Show;
import com.nisha.bookmyshow.entity.ShowSeat;
import com.nisha.bookmyshow.entity.ShowSeatStatus;
import com.nisha.bookmyshow.entity.Theater;
import com.nisha.bookmyshow.entity.User;
import com.nisha.bookmyshow.entity.Role;
import com.nisha.bookmyshow.exception.SeatNotAvailableException;
import com.nisha.bookmyshow.repository.BookingRepository;
import com.nisha.bookmyshow.repository.ShowSeatRepository;
import com.nisha.bookmyshow.util.SecurityUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ShowSeatRepository showSeatRepository;
    @Mock
    private ShowService showService;
    @Mock
    private SecurityUtil securityUtil;
    @Mock
    private PaymentService paymentService;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void throwsWhenSeatAlreadyBooked() {
        User user = User.builder().id(1L).name("A").email("a@test.local").role(Role.USER).build();
        Show show = show(10L);
        Seat seat = Seat.builder().id(3L).seatNumber("A1").rowNumber("A").seatType(SeatType.REGULAR).price(BigDecimal.TEN).build();
        ShowSeat showSeat = ShowSeat.builder().id(5L).show(show).seat(seat).status(ShowSeatStatus.BOOKED).price(BigDecimal.TEN).build();

        when(securityUtil.currentUser()).thenReturn(user);
        when(showService.findDetailed(10L)).thenReturn(show);
        when(showSeatRepository.lockSeatsForShow(List.of(5L), 10L)).thenReturn(List.of(showSeat));

        SeatNotAvailableException ex = assertThrows(SeatNotAvailableException.class,
                () -> bookingService.create(new BookingRequest(10L, List.of(5L))));
        assertTrue(ex.getMessage().contains("A1"));
        assertTrue(ex.getMessage().contains("no longer available"));
    }

    private Show show(Long id) {
        return Show.builder()
                .id(id)
                .movie(Movie.builder().id(1L).title("Avengers").build())
                .theater(Theater.builder().id(1L).name("PVR").build())
                .screen(Screen.builder().id(1L).name("Screen 2").build())
                .showDate(LocalDate.of(2026, 8, 20))
                .startTime(LocalTime.of(19, 30))
                .endTime(LocalTime.of(22, 30))
                .build();
    }
}
