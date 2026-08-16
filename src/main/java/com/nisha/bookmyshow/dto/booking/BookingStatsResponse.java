package com.nisha.bookmyshow.dto.booking;

public record BookingStatsResponse(
        long totalBookings,
        long confirmed,
        long cancelled,
        long pending
) {
}
