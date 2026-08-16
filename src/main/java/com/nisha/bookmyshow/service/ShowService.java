package com.nisha.bookmyshow.service;

import com.nisha.bookmyshow.dto.seat.SeatResponse;
import com.nisha.bookmyshow.dto.show.ShowRequest;
import com.nisha.bookmyshow.dto.show.ShowResponse;
import com.nisha.bookmyshow.entity.Movie;
import com.nisha.bookmyshow.entity.Screen;
import com.nisha.bookmyshow.entity.Seat;
import com.nisha.bookmyshow.entity.Show;
import com.nisha.bookmyshow.entity.ShowSeat;
import com.nisha.bookmyshow.entity.ShowSeatStatus;
import com.nisha.bookmyshow.entity.Theater;
import com.nisha.bookmyshow.exception.ApiException;
import com.nisha.bookmyshow.exception.ResourceNotFoundException;
import com.nisha.bookmyshow.repository.BookingRepository;
import com.nisha.bookmyshow.repository.SeatRepository;
import com.nisha.bookmyshow.repository.ShowRepository;
import com.nisha.bookmyshow.repository.ShowSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowService {

    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final MovieService movieService;
    private final TheaterService theaterService;
    private final ScreenService screenService;

    public Page<ShowResponse> search(Long movieId, Long theaterId, LocalDate date, Pageable pageable) {
        return showRepository.search(movieId, theaterId, date, pageable).map(ShowResponse::from);
    }

    public ShowResponse get(Long id) {
        return ShowResponse.from(findDetailed(id));
    }

    public List<SeatResponse> seats(Long showId) {
        findDetailed(showId);
        return showSeatRepository.findByShowIdWithSeat(showId).stream().map(SeatResponse::from).toList();
    }

    @Transactional
    public ShowResponse create(ShowRequest request) {
        validateTimes(request);
        Movie movie = movieService.find(request.movieId());
        Theater theater = theaterService.find(request.theaterId());
        Screen screen = screenService.find(request.screenId());
        if (!screen.getTheater().getId().equals(theater.getId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Screen does not belong to the selected theater");
        }
        List<Seat> seats = seatRepository.findByScreenIdOrderByRowNumberAscSeatNumberAsc(screen.getId());
        if (seats.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Add seats to the screen before creating a show");
        }
        assertNoScheduleConflict(screen.getId(), request.showDate(), request.startTime(), request.endTime(), null);
        Show show = showRepository.save(Show.builder()
                .movie(movie)
                .theater(theater)
                .screen(screen)
                .showDate(request.showDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .build());
        List<ShowSeat> showSeats = seats.stream()
                .map(seat -> ShowSeat.builder()
                        .show(show)
                        .seat(seat)
                        .status(ShowSeatStatus.AVAILABLE)
                        .price(seat.getPrice())
                        .build())
                .toList();
        showSeatRepository.saveAll(showSeats);
        return ShowResponse.from(findDetailed(show.getId()));
    }

    @Transactional
    public ShowResponse update(Long id, ShowRequest request) {
        validateTimes(request);
        Show show = findDetailed(id);
        if (!show.getTheater().getId().equals(request.theaterId())
                || !show.getScreen().getId().equals(request.screenId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot change theater or screen of an existing show");
        }
        assertNoScheduleConflict(show.getScreen().getId(), request.showDate(), request.startTime(), request.endTime(), id);
        show.setMovie(movieService.find(request.movieId()));
        show.setShowDate(request.showDate());
        show.setStartTime(request.startTime());
        show.setEndTime(request.endTime());
        showRepository.save(show);
        return ShowResponse.from(findDetailed(id));
    }

    @Transactional
    public void delete(Long id) {
        Show show = findDetailed(id);
        if (bookingRepository.existsByShowId(id)) {
            throw new ApiException(HttpStatus.CONFLICT, "Cannot delete a show that already has bookings");
        }
        showSeatRepository.deleteByShowId(id);
        showRepository.delete(show);
    }

    public Show findDetailed(Long id) {
        return showRepository.findDetailedById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found: " + id));
    }

    private void validateTimes(ShowRequest request) {
        if (!request.endTime().isAfter(request.startTime())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Show end time must be after start time");
        }
    }

    private void assertNoScheduleConflict(Long screenId, LocalDate date, java.time.LocalTime start,
                                          java.time.LocalTime end, Long excludeShowId) {
        List<Show> existing = showRepository.findByScreenIdAndShowDate(screenId, date);
        boolean overlap = existing.stream()
                .filter(s -> excludeShowId == null || !s.getId().equals(excludeShowId))
                .anyMatch(s -> start.isBefore(s.getEndTime()) && end.isAfter(s.getStartTime()));
        if (overlap) {
            throw new ApiException(HttpStatus.CONFLICT, "Another show is already scheduled on this screen at that time");
        }
    }
}
