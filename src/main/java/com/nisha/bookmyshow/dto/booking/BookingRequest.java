package com.nisha.bookmyshow.dto.booking;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookingRequest(
        @NotNull Long showId,
        @NotEmpty List<Long> showSeatIds
) {
}
