package com.example.retail.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoyaltyUpdateRequest(
        @NotNull
        @Min(-10000)
        @Max(10000)
        Integer pointsDelta,

        @NotBlank
        String reason
) {
}
