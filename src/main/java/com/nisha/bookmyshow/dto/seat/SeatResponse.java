package com.nisha.bookmyshow.dto.seat;

import com.nisha.bookmyshow.entity.Seat;
import com.nisha.bookmyshow.entity.SeatType;
import com.nisha.bookmyshow.entity.ShowSeat;
import com.nisha.bookmyshow.entity.ShowSeatStatus;

import java.math.BigDecimal;

public record SeatResponse(
        Long id,
        Long showSeatId,
        String seatNumber,
        String rowNumber,
        SeatType seatType,
        BigDecimal price,
        ShowSeatStatus status
) {
    public static SeatResponse from(Seat s) {
        return new SeatResponse(s.getId(), null, s.getSeatNumber(), s.getRowNumber(), s.getSeatType(), s.getPrice(), null);
    }

    public static SeatResponse from(ShowSeat ss) {
        Seat s = ss.getSeat();
        return new SeatResponse(s.getId(), ss.getId(), s.getSeatNumber(), s.getRowNumber(),
                s.getSeatType(), ss.getPrice(), ss.getStatus());
    }
}
