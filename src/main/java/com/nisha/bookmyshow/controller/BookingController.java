package com.nisha.bookmyshow.controller;

import com.nisha.bookmyshow.dto.ApiResponse;
import com.nisha.bookmyshow.dto.booking.BookingRequest;
import com.nisha.bookmyshow.dto.booking.BookingResponse;
import com.nisha.bookmyshow.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Confirm a booking for selected show seats")
    public ApiResponse<BookingResponse> create(@Valid @RequestBody BookingRequest request) {
        return ApiResponse.ok("Booking confirmed", bookingService.create(request));
    }

    @GetMapping
    @Operation(summary = "List the current user's bookings")
    public ApiResponse<Page<BookingResponse>> mine(@PageableDefault(size = 10, sort = "bookingDate") Pageable pageable) {
        return ApiResponse.ok(bookingService.myBookings(pageable));
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Upcoming confirmed bookings")
    public ApiResponse<Page<BookingResponse>> upcoming(@PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.ok(bookingService.upcoming(pageable));
    }

    @GetMapping("/past")
    @Operation(summary = "Past or cancelled bookings")
    public ApiResponse<Page<BookingResponse>> past(@PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.ok(bookingService.past(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking details (owner or admin)")
    public ApiResponse<BookingResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(bookingService.get(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel an eligible booking")
    public ApiResponse<BookingResponse> cancel(@PathVariable Long id) {
        return ApiResponse.ok("Booking cancelled", bookingService.cancel(id));
    }
}
