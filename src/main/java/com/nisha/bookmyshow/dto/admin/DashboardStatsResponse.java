package com.nisha.bookmyshow.dto.admin;

public record DashboardStatsResponse(
        long totalUsers,
        long totalMovies,
        long totalTheaters,
        long totalShows,
        long totalBookings,
        long confirmedBookings,
        long cancelledBookings
) {
}
