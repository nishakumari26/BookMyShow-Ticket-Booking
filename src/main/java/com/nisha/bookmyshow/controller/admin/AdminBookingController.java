package com.nisha.bookmyshow.controller.admin;

import com.nisha.bookmyshow.dto.ApiResponse;
import com.nisha.bookmyshow.dto.booking.BookingResponse;
import com.nisha.bookmyshow.dto.booking.BookingStatsResponse;
import com.nisha.bookmyshow.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/bookings")
@RequiredArgsConstructor
@Tag(name = "Admin — Bookings")
@SecurityRequirement(name = "bearerAuth")
public class AdminBookingController {

    private final BookingService bookingService;

    @GetMapping
    @Operation(summary = "List all bookings")
    public ApiResponse<Page<BookingResponse>> list(@PageableDefault(size = 20, sort = "bookingDate") Pageable pageable) {
        return ApiResponse.ok(bookingService.adminList(pageable));
    }

    @GetMapping("/stats")
    @Operation(summary = "Booking statistics")
    public ApiResponse<BookingStatsResponse> stats() {
        return ApiResponse.ok(bookingService.stats());
    }
}
