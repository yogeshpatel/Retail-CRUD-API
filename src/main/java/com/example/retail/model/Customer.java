package com.example.retail.model;

import java.time.LocalDate;

public class Customer {

    private Long      id;
    private String    name;
    private String    email;
    private int       loyaltyPoints;
    private boolean   active;
    private LocalDate joinedDate;

    public Customer() {
    }

    public Customer(Long id, String name, String email, int loyaltyPoints,
                    boolean active, LocalDate joinedDate) {
        this.id            = id;
        this.name          = name;
        this.email         = email;
        this.loyaltyPoints = loyaltyPoints;
        this.active        = active;
        this.joinedDate    = joinedDate;
    }

    public Long getId()                    { return id; }
    public String getName()                { return name; }
    public String getEmail()               { return email; }
    public int getLoyaltyPoints()          { return loyaltyPoints; }
    public boolean isActive()              { return active; }
    public LocalDate getJoinedDate()       { return joinedDate; }

    public void setId(Long id)                        { this.id = id; }
    public void setName(String name)                  { this.name = name; }
    public void setEmail(String email)                { this.email = email; }
    public void setLoyaltyPoints(int loyaltyPoints)   { this.loyaltyPoints = loyaltyPoints; }
    public void setActive(boolean active)             { this.active = active; }
    public void setJoinedDate(LocalDate joinedDate)   { this.joinedDate = joinedDate; }
}
