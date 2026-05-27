package com.example.retail.dto;

import java.math.BigDecimal;

public record OrderResponse(
        Long id,
        String orderNumber,
        BigDecimal totalAmount,
        String status
) {
}
