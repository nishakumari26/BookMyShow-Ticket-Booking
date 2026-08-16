package com.nisha.bookmyshow.dto.seat;

import com.nisha.bookmyshow.entity.SeatType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record SeatRequest(
        @NotBlank String seatNumber,
        @NotBlank String rowNumber,
        @NotNull SeatType seatType,
        @NotNull @Positive BigDecimal price
) {
}
