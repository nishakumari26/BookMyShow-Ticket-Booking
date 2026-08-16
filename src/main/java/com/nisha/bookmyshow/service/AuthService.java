package com.nisha.bookmyshow.service;

import com.nisha.bookmyshow.dto.auth.AuthResponse;
import com.nisha.bookmyshow.dto.auth.LoginRequest;
import com.nisha.bookmyshow.dto.auth.RegisterRequest;
import com.nisha.bookmyshow.entity.Role;
import com.nisha.bookmyshow.entity.User;
import com.nisha.bookmyshow.exception.ApiException;
import com.nisha.bookmyshow.repository.UserRepository;
import com.nisha.bookmyshow.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email().toLowerCase())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email is already registered");
        }
        User user = User.builder()
                .name(request.name())
                .email(request.email().toLowerCase())
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .role(Role.USER)
                .build();
        user = userRepository.save(user);
        return toAuth(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().toLowerCase(), request.password()));
        User user = userRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        return toAuth(user);
    }

    private AuthResponse toAuth(User user) {
        return new AuthResponse(jwtService.generateToken(user), user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
