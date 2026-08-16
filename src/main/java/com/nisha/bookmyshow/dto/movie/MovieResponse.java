package com.nisha.bookmyshow.dto.movie;

import com.nisha.bookmyshow.entity.Movie;
import com.nisha.bookmyshow.entity.MovieStatus;

import java.time.LocalDate;

public record MovieResponse(
        Long id,
        String title,
        String description,
        String language,
        String genre,
        Integer duration,
        LocalDate releaseDate,
        String posterUrl,
        Double rating,
        MovieStatus status
) {
    public static MovieResponse from(Movie m) {
        return new MovieResponse(m.getId(), m.getTitle(), m.getDescription(), m.getLanguage(),
                m.getGenre(), m.getDuration(), m.getReleaseDate(), m.getPosterUrl(), m.getRating(), m.getStatus());
    }
}
