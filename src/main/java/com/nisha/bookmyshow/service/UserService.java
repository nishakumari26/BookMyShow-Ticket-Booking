package com.nisha.bookmyshow.service;

import com.nisha.bookmyshow.dto.user.ChangePasswordRequest;
import com.nisha.bookmyshow.dto.user.UpdateProfileRequest;
import com.nisha.bookmyshow.dto.user.UserResponse;
import com.nisha.bookmyshow.entity.User;
import com.nisha.bookmyshow.exception.ApiException;
import com.nisha.bookmyshow.repository.UserRepository;
import com.nisha.bookmyshow.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;

    public UserResponse profile() {
        return UserResponse.from(securityUtil.currentUser());
    }

    public UserResponse updateProfile(UpdateProfileRequest request) {
        User user = securityUtil.currentUser();
        if (request.name() != null) {
            user.setName(request.name());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }
        return UserResponse.from(userRepository.save(user));
    }

    public void changePassword(ChangePasswordRequest request) {
        User user = securityUtil.currentUser();
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
}
