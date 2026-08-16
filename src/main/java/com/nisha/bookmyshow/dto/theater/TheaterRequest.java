package com.nisha.bookmyshow.dto.theater;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TheaterRequest(
        @NotBlank @Size(max = 120) String name,
        String location,
        @NotBlank String city,
        String address
) {
}
