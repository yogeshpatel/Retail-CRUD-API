package com.example.retail.model;

import java.math.BigDecimal;

public class CustomerOrder {

    private Long id;
    private Long customerId;
    private String orderNumber;
    private BigDecimal totalAmount;
    private String status;

    public CustomerOrder() {
    }

    public CustomerOrder(Long id, Long customerId, String orderNumber, BigDecimal totalAmount, String status) {
        this.id = id;
        this.customerId = customerId;
        this.orderNumber = orderNumber;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
