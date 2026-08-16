package com.nisha.bookmyshow.dto.screen;

import com.nisha.bookmyshow.entity.Screen;

public record ScreenResponse(Long id, String name, Long theaterId, String theaterName) {
    public static ScreenResponse from(Screen s) {
        return new ScreenResponse(s.getId(), s.getName(), s.getTheater().getId(), s.getTheater().getName());
    }
}
