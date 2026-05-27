package com.example.retail.dto;

public record OfferResponse(
        String code,
        int discountPercentage,
        boolean active
) {
}
