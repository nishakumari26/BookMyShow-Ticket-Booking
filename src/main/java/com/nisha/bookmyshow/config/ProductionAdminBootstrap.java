package com.nisha.bookmyshow.config;

import com.nisha.bookmyshow.entity.Role;
import com.nisha.bookmyshow.entity.User;
import com.nisha.bookmyshow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductionAdminBootstrap implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (!StringUtils.hasText(adminEmail) || !StringUtils.hasText(adminPassword)) {
            return;
        }
        if (adminPassword.length() < 8) {
            throw new IllegalStateException("APP_ADMIN_PASSWORD must be at least 8 characters.");
        }
        String email = adminEmail.trim().toLowerCase();
        userRepository.findByEmail(email).ifPresentOrElse(user -> {
            if (user.getRole() != Role.ADMIN) {
                user.setRole(Role.ADMIN);
                userRepository.save(user);
                log.info("Promoted {} to ADMIN", email);
            }
        }, () -> {
            userRepository.save(User.builder()
                    .name("Admin")
                    .email(email)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .build());
            log.info("Created ADMIN user {}", email);
        });
    }
}
