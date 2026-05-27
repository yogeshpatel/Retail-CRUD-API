package com.example.retail.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

// NOTE: "ORDER" is a reserved SQL keyword, so the table is named "customer_order"
@Entity
@Table(name = "customer_order")
public class CustomerOrder {

    @Id
    private Long id;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private String status;

    public CustomerOrder() {
    }

    public CustomerOrder(Long id, Long customerId, String orderNumber,
                         BigDecimal totalAmount, String status) {
        this.id          = id;
        this.customerId  = customerId;
        this.orderNumber = orderNumber;
        this.totalAmount = totalAmount;
        this.status      = status;
    }

    public Long getId()              { return id; }
    public Long getCustomerId()      { return customerId; }
    public String getOrderNumber()   { return orderNumber; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getStatus()        { return status; }

    public void setId(Long id)                    { this.id = id; }
    public void setCustomerId(Long customerId)    { this.customerId = customerId; }
    public void setOrderNumber(String orderNumber){ this.orderNumber = orderNumber; }
    public void setTotalAmount(BigDecimal amt)    { this.totalAmount = amt; }
    public void setStatus(String status)          { this.status = status; }
}
