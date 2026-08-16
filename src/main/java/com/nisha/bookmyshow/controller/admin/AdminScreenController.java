package com.nisha.bookmyshow.controller.admin;

import com.nisha.bookmyshow.dto.ApiResponse;
import com.nisha.bookmyshow.dto.screen.ScreenRequest;
import com.nisha.bookmyshow.dto.screen.ScreenResponse;
import com.nisha.bookmyshow.dto.seat.SeatRequest;
import com.nisha.bookmyshow.dto.seat.SeatResponse;
import com.nisha.bookmyshow.service.ScreenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin — Screens & Seats")
@SecurityRequirement(name = "bearerAuth")
public class AdminScreenController {

    private final ScreenService screenService;

    @PostMapping("/screens")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a screen")
    public ApiResponse<ScreenResponse> create(@Valid @RequestBody ScreenRequest request) {
        return ApiResponse.ok("Screen created", screenService.create(request));
    }

    @PutMapping("/screens/{id}")
    @Operation(summary = "Update a screen")
    public ApiResponse<ScreenResponse> update(@PathVariable Long id, @Valid @RequestBody ScreenRequest request) {
        return ApiResponse.ok("Screen updated", screenService.update(id, request));
    }

    @DeleteMapping("/screens/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a screen")
    public void delete(@PathVariable Long id) {
        screenService.delete(id);
    }

    @PostMapping("/screens/{screenId}/seats")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a seat to a screen")
    public ApiResponse<SeatResponse> addSeat(@PathVariable Long screenId, @Valid @RequestBody SeatRequest request) {
        return ApiResponse.ok("Seat added", screenService.addSeat(screenId, request));
    }

    @GetMapping("/screens/{screenId}/seats")
    @Operation(summary = "List template seats for a screen")
    public ApiResponse<List<SeatResponse>> seats(@PathVariable Long screenId) {
        return ApiResponse.ok(screenService.seats(screenId));
    }

    @PutMapping("/seats/{seatId}")
    @Operation(summary = "Update a seat")
    public ApiResponse<SeatResponse> updateSeat(@PathVariable Long seatId, @Valid @RequestBody SeatRequest request) {
        return ApiResponse.ok("Seat updated", screenService.updateSeat(seatId, request));
    }

    @DeleteMapping("/seats/{seatId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a seat")
    public void deleteSeat(@PathVariable Long seatId) {
        screenService.deleteSeat(seatId);
    }
}
