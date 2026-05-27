package com.example.retail.dto;

import java.time.LocalDate;
import java.util.List;

public record CustomerProfileResponse(
        Long             id,
        String           name,
        String           email,
        int              loyaltyPoints,
        boolean          active,
        LocalDate        joinedDate,
        List<OrderResponse> orderHistory
) {
}
