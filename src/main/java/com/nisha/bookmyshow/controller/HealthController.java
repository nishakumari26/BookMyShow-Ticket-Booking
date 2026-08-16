package com.nisha.bookmyshow.controller;

import com.nisha.bookmyshow.dto.ApiResponse;
import com.nisha.bookmyshow.repository.MovieRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Health")
public class HealthController {

    private final MovieRepository movieRepository;

    @GetMapping("/health")
    @Operation(summary = "Liveness and database connectivity")
    public ApiResponse<Map<String, Object>> health() {
        long movies = movieRepository.count();
        return ApiResponse.ok(Map.of(
                "status", "UP",
                "database", "UP",
                "movies", movies,
                "timestamp", Instant.now().toString()
        ));
    }
}
