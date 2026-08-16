package com.nisha.bookmyshow;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class BookMyShowApplicationTests extends AbstractApiTest {

    @Test
    void contextLoads() {
        // Spring context and security filter chain start successfully
    }
}
