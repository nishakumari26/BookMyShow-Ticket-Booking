package com.nisha.bookmyshow.controller.admin;

import com.nisha.bookmyshow.dto.ApiResponse;
import com.nisha.bookmyshow.dto.admin.DashboardStatsResponse;
import com.nisha.bookmyshow.dto.user.UserResponse;
import com.nisha.bookmyshow.entity.BookingStatus;
import com.nisha.bookmyshow.repository.BookingRepository;
import com.nisha.bookmyshow.repository.MovieRepository;
import com.nisha.bookmyshow.repository.ShowRepository;
import com.nisha.bookmyshow.repository.TheaterRepository;
import com.nisha.bookmyshow.repository.UserRepository;
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
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin — Dashboard")
@SecurityRequirement(name = "bearerAuth")
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final ShowRepository showRepository;
    private final BookingRepository bookingRepository;

    @GetMapping("/dashboard")
    @Operation(summary = "Admin dashboard counts")
    public ApiResponse<DashboardStatsResponse> dashboard() {
        return ApiResponse.ok(new DashboardStatsResponse(
                userRepository.count(),
                movieRepository.count(),
                theaterRepository.count(),
                showRepository.count(),
                bookingRepository.count(),
                bookingRepository.countByBookingStatus(BookingStatus.CONFIRMED),
                bookingRepository.countByBookingStatus(BookingStatus.CANCELLED)
        ));
    }

    @GetMapping("/users")
    @Operation(summary = "List users")
    public ApiResponse<Page<UserResponse>> users(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ApiResponse.ok(userRepository.findAll(pageable).map(UserResponse::from));
    }
}
