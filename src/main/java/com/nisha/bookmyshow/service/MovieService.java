package com.nisha.bookmyshow.service;

import com.nisha.bookmyshow.dto.movie.MovieRequest;
import com.nisha.bookmyshow.dto.movie.MovieResponse;
import com.nisha.bookmyshow.entity.Movie;
import com.nisha.bookmyshow.entity.MovieStatus;
import com.nisha.bookmyshow.exception.ApiException;
import com.nisha.bookmyshow.exception.ResourceNotFoundException;
import com.nisha.bookmyshow.repository.MovieRepository;
import com.nisha.bookmyshow.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;

    public Page<MovieResponse> search(String q, String language, String genre, MovieStatus status, Pageable pageable) {
        String query = (q == null || q.isBlank()) ? null : q.trim();
        return movieRepository.search(query, emptyToNull(language), emptyToNull(genre), status, pageable)
                .map(MovieResponse::from);
    }

    public MovieResponse get(Long id) {
        return MovieResponse.from(find(id));
    }

    public MovieResponse create(MovieRequest request) {
        return MovieResponse.from(movieRepository.save(toEntity(new Movie(), request)));
    }

    public MovieResponse update(Long id, MovieRequest request) {
        Movie movie = find(id);
        return MovieResponse.from(movieRepository.save(toEntity(movie, request)));
    }

    public void delete(Long id) {
        if (showRepository.existsByMovieId(id)) {
            throw new ApiException(HttpStatus.CONFLICT, "Cannot delete a movie that has scheduled shows");
        }
        movieRepository.delete(find(id));
    }

    public Movie find(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found: " + id));
    }

    private Movie toEntity(Movie movie, MovieRequest request) {
        movie.setTitle(request.title());
        movie.setDescription(request.description());
        movie.setLanguage(request.language());
        movie.setGenre(request.genre());
        movie.setDuration(request.duration());
        movie.setReleaseDate(request.releaseDate());
        movie.setPosterUrl(request.posterUrl());
        movie.setRating(request.rating());
        movie.setStatus(request.status());
        return movie;
    }

    private String emptyToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
