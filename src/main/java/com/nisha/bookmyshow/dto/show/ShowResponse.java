package com.nisha.bookmyshow.dto.show;

import com.nisha.bookmyshow.entity.Show;

import java.time.LocalDate;
import java.time.LocalTime;

public record ShowResponse(
        Long id,
        Long movieId,
        String movieTitle,
        Long theaterId,
        String theaterName,
        Long screenId,
        String screenName,
        LocalDate showDate,
        LocalTime startTime,
        LocalTime endTime
) {
    public static ShowResponse from(Show s) {
        return new ShowResponse(
                s.getId(),
                s.getMovie().getId(),
                s.getMovie().getTitle(),
                s.getTheater().getId(),
                s.getTheater().getName(),
                s.getScreen().getId(),
                s.getScreen().getName(),
                s.getShowDate(),
                s.getStartTime(),
                s.getEndTime()
        );
    }
}
