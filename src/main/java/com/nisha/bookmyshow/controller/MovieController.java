package com.nisha.bookmyshow.controller;

import com.nisha.bookmyshow.dto.ApiResponse;
import com.nisha.bookmyshow.dto.movie.MovieResponse;
import com.nisha.bookmyshow.entity.MovieStatus;
import com.nisha.bookmyshow.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Tag(name = "Movies")
public class MovieController {

    private final MovieService movieService;

    @GetMapping({ "", "/search" })
    @Operation(summary = "Search and list movies")
    public ApiResponse<Page<MovieResponse>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) MovieStatus status,
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        return ApiResponse.ok(movieService.search(q, language, genre, status, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get movie by ID")
    public ApiResponse<MovieResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(movieService.get(id));
    }
}
