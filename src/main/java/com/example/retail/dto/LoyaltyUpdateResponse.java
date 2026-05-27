package com.example.retail.dto;

public record LoyaltyUpdateResponse(
        Long customerId,
        int previousLoyaltyPoints,
        int updatedLoyaltyPoints,
        int pointsDelta,
        String reason
) {
}
