package com.nisha.bookmyshow.dto.screen;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ScreenRequest(
        @NotBlank String name,
        @NotNull Long theaterId
) {
}
