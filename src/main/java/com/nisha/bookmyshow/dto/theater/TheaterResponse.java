package com.nisha.bookmyshow.dto.theater;

import com.nisha.bookmyshow.entity.Theater;

public record TheaterResponse(Long id, String name, String location, String city, String address) {
    public static TheaterResponse from(Theater t) {
        return new TheaterResponse(t.getId(), t.getName(), t.getLocation(), t.getCity(), t.getAddress());
    }
}
