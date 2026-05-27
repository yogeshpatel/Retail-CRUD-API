package com.example.retail.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateOfferRequest(

        @NotBlank(message = "code must not be blank")
        @Pattern(regexp = "^[A-Za-z0-9_-]{2,20}$",
                 message = "code must be 2-20 alphanumeric characters (A-Z, 0-9, _ or -)")
        String code,

        @Min(value = 1,   message = "discountPercent must be between 1 and 100")
        @Max(value = 100, message = "discountPercent must be between 1 and 100")
        int discountPercent,

        boolean active
) {
}
