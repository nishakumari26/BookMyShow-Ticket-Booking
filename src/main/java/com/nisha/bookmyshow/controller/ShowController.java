package com.nisha.bookmyshow.controller;

import com.nisha.bookmyshow.dto.ApiResponse;
import com.nisha.bookmyshow.dto.seat.SeatResponse;
import com.nisha.bookmyshow.dto.show.ShowResponse;
import com.nisha.bookmyshow.service.ShowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
@Tag(name = "Shows")
public class ShowController {

    private final ShowService showService;

    @GetMapping
    @Operation(summary = "Search shows by movie, theater, and/or date")
    public ApiResponse<Page<ShowResponse>> search(
            @RequestParam(required = false) Long movieId,
            @RequestParam(required = false) Long theaterId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PageableDefault(size = 10, sort = "showDate") Pageable pageable) {
        return ApiResponse.ok(showService.search(movieId, theaterId, date, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get show by ID")
    public ApiResponse<ShowResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(showService.get(id));
    }

    @GetMapping("/{showId}/seats")
    @Operation(summary = "List seat availability for a show")
    public ApiResponse<List<SeatResponse>> seats(@PathVariable Long showId) {
        return ApiResponse.ok(showService.seats(showId));
    }
}
