package com.nisha.bookmyshow.dto.user;

import com.nisha.bookmyshow.entity.Role;
import com.nisha.bookmyshow.entity.User;

public record UserResponse(Long id, String name, String email, String phone, Role role) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getRole());
    }
}
