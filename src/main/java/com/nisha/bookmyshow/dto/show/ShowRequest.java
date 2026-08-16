package com.nisha.bookmyshow.dto.show;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ShowRequest(
        @NotNull Long movieId,
        @NotNull Long theaterId,
        @NotNull Long screenId,
        @NotNull LocalDate showDate,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime
) {
}
