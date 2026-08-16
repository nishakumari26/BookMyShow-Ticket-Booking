package com.nisha.bookmyshow.controller;

import com.nisha.bookmyshow.dto.ApiResponse;
import com.nisha.bookmyshow.dto.screen.ScreenResponse;
import com.nisha.bookmyshow.dto.theater.TheaterResponse;
import com.nisha.bookmyshow.service.ScreenService;
import com.nisha.bookmyshow.service.TheaterService;
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

import java.util.List;

@RestController
@RequestMapping("/api/theaters")
@RequiredArgsConstructor
@Tag(name = "Theaters")
public class TheaterController {

    private final TheaterService theaterService;
    private final ScreenService screenService;

    @GetMapping
    @Operation(summary = "List theaters, optionally filtered by city")
    public ApiResponse<Page<TheaterResponse>> list(
            @RequestParam(required = false) String city,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ApiResponse.ok(theaterService.list(city, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get theater by ID")
    public ApiResponse<TheaterResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(theaterService.get(id));
    }

    @GetMapping("/{id}/screens")
    @Operation(summary = "Get screens for a theater")
    public ApiResponse<List<ScreenResponse>> screens(@PathVariable Long id) {
        return ApiResponse.ok(screenService.byTheater(id));
    }
}
