package com.nisha.bookmyshow.controller;

import com.nisha.bookmyshow.dto.ApiResponse;
import com.nisha.bookmyshow.dto.user.ChangePasswordRequest;
import com.nisha.bookmyshow.dto.user.UpdateProfileRequest;
import com.nisha.bookmyshow.dto.user.UserResponse;
import com.nisha.bookmyshow.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping({"/me", "/profile"})
    @Operation(summary = "Get the authenticated user's profile")
    public ApiResponse<UserResponse> profile() {
        return ApiResponse.ok(userService.profile());
    }

    @PutMapping({"/me", "/profile"})
    @Operation(summary = "Update name or phone")
    public ApiResponse<UserResponse> update(@Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.ok("Profile updated", userService.updateProfile(request));
    }

    @PutMapping({"/me/password", "/change-password"})
    @Operation(summary = "Change password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ApiResponse.ok("Password changed", null);
    }
}
