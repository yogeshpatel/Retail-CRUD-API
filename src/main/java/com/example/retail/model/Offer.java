package com.example.retail.model;

public class Offer {

    private String code;
    private int discountPercent;
    private boolean active;

    public Offer() {
    }

    public Offer(String code, int discountPercent, boolean active) {
        this.code = code;
        this.discountPercent = discountPercent;
        this.active = active;
    }

    public String getCode() {
        return code;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public boolean isActive() {
        return active;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
