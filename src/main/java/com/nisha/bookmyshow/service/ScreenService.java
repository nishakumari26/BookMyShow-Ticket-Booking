package com.nisha.bookmyshow.service;

import com.nisha.bookmyshow.dto.screen.ScreenRequest;
import com.nisha.bookmyshow.dto.screen.ScreenResponse;
import com.nisha.bookmyshow.dto.seat.SeatRequest;
import com.nisha.bookmyshow.dto.seat.SeatResponse;
import com.nisha.bookmyshow.entity.Screen;
import com.nisha.bookmyshow.entity.Seat;
import com.nisha.bookmyshow.entity.Theater;
import com.nisha.bookmyshow.exception.ApiException;
import com.nisha.bookmyshow.exception.ResourceNotFoundException;
import com.nisha.bookmyshow.repository.ScreenRepository;
import com.nisha.bookmyshow.repository.SeatRepository;
import com.nisha.bookmyshow.repository.ShowRepository;
import com.nisha.bookmyshow.repository.ShowSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScreenService {

    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final TheaterService theaterService;

    public ScreenResponse create(ScreenRequest request) {
        Theater theater = theaterService.find(request.theaterId());
        Screen screen = Screen.builder().name(request.name()).theater(theater).build();
        return ScreenResponse.from(screenRepository.save(screen));
    }

    public ScreenResponse update(Long id, ScreenRequest request) {
        Screen screen = find(id);
        screen.setName(request.name());
        screen.setTheater(theaterService.find(request.theaterId()));
        return ScreenResponse.from(screenRepository.save(screen));
    }

    public void delete(Long id) {
        if (showRepository.existsByScreenId(id)) {
            throw new ApiException(HttpStatus.CONFLICT, "Cannot delete a screen that has scheduled shows");
        }
        screenRepository.delete(find(id));
    }

    public List<ScreenResponse> byTheater(Long theaterId) {
        theaterService.find(theaterId);
        return screenRepository.findByTheaterId(theaterId).stream().map(ScreenResponse::from).toList();
    }

    public SeatResponse addSeat(Long screenId, SeatRequest request) {
        Screen screen = find(screenId);
        Seat seat = Seat.builder()
                .seatNumber(request.seatNumber())
                .rowNumber(request.rowNumber())
                .seatType(request.seatType())
                .price(request.price())
                .screen(screen)
                .build();
        return SeatResponse.from(seatRepository.save(seat));
    }

    public List<SeatResponse> seats(Long screenId) {
        find(screenId);
        return seatRepository.findByScreenIdOrderByRowNumberAscSeatNumberAsc(screenId)
                .stream().map(SeatResponse::from).toList();
    }

    public SeatResponse updateSeat(Long seatId, SeatRequest request) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found: " + seatId));
        seat.setSeatNumber(request.seatNumber());
        seat.setRowNumber(request.rowNumber());
        seat.setSeatType(request.seatType());
        seat.setPrice(request.price());
        return SeatResponse.from(seatRepository.save(seat));
    }

    public void deleteSeat(Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found: " + seatId));
        if (showSeatRepository.existsBySeatId(seatId)) {
            throw new ApiException(HttpStatus.CONFLICT, "Cannot delete a seat that is used in shows");
        }
        seatRepository.delete(seat);
    }

    public Screen find(Long id) {
        return screenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found: " + id));
    }
}
