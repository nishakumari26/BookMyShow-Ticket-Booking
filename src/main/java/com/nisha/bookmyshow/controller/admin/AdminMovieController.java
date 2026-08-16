package com.nisha.bookmyshow.controller.admin;

import com.nisha.bookmyshow.dto.ApiResponse;
import com.nisha.bookmyshow.dto.movie.MovieRequest;
import com.nisha.bookmyshow.dto.movie.MovieResponse;
import com.nisha.bookmyshow.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/movies")
@RequiredArgsConstructor
@Tag(name = "Admin — Movies")
@SecurityRequirement(name = "bearerAuth")
public class AdminMovieController {

    private final MovieService movieService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a movie")
    public ApiResponse<MovieResponse> create(@Valid @RequestBody MovieRequest request) {
        return ApiResponse.ok("Movie created", movieService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a movie")
    public ApiResponse<MovieResponse> update(@PathVariable Long id, @Valid @RequestBody MovieRequest request) {
        return ApiResponse.ok("Movie updated", movieService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a movie")
    public void delete(@PathVariable Long id) {
        movieService.delete(id);
    }
}
