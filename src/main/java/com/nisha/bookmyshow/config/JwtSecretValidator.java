package com.nisha.bookmyshow.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

@Component
public class JwtSecretValidator implements ApplicationRunner {

    private static final Set<String> FORBIDDEN = Set.of(
            "dev-only-change-me-use-env-in-prod-32chars",
            "dev-only-local-not-for-production-use-32",
            "change-this-to-a-long-random-secret-at-least-32-chars",
            "docker-dev-secret-change-in-production-32"
    );

    private final String secret;
    private final Environment environment;

    public JwtSecretValidator(@Value("${app.jwt.secret}") String secret, Environment environment) {
        this.secret = secret;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET must be set and at least 32 characters.");
        }
        boolean prod = Arrays.stream(environment.getActiveProfiles())
                .anyMatch(p -> p.equalsIgnoreCase("prod"));
        if (prod && FORBIDDEN.contains(secret.toLowerCase(Locale.ROOT))) {
            throw new IllegalStateException("JWT_SECRET is a development placeholder. Set a unique production secret.");
        }
    }
}
