package com.nisha.bookmyshow.dto.user;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(min = 2, max = 80) String name,
        @Size(max = 15) String phone
) {
}
