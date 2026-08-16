package com.nisha.bookmyshow.dto.movie;

import com.nisha.bookmyshow.entity.MovieStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record MovieRequest(
        @NotBlank @Size(max = 150) String title,
        @Size(max = 2000) String description,
        String language,
        String genre,
        Integer duration,
        LocalDate releaseDate,
        String posterUrl,
        Double rating,
        @NotNull MovieStatus status
) {
}
