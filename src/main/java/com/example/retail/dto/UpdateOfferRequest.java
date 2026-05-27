package com.example.retail.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdateOfferRequest(

        @Min(value = 1,   message = "discountPercent must be between 1 and 100")
        @Max(value = 100, message = "discountPercent must be between 1 and 100")
        int discountPercent,

        boolean active
) {
}
