package com.example.retail.controller;

import com.example.retail.dto.CustomerProfileResponse;
import com.example.retail.dto.LoyaltyUpdateRequest;
import com.example.retail.dto.LoyaltyUpdateResponse;
import com.example.retail.dto.OfferResponse;
import com.example.retail.model.Customer;
import com.example.retail.service.RetailService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Validated
public class RetailController {

    private final RetailService retailService;

    public RetailController(RetailService retailService) {
        this.retailService = retailService;
    }

    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        return retailService.getAllCustomers();
    }

    @GetMapping("/customers/{id}")
    public CustomerProfileResponse getCustomer(@PathVariable @Positive Long id) {
        return retailService.getCustomerProfile(id);
    }

    @GetMapping("/offers")
    public List<OfferResponse> getAllOffers() {
        return retailService.getAllOffers();
    }

    @GetMapping("/offers/{code}")
    public OfferResponse getOffer(@PathVariable String code) {
        return retailService.getOffer(code);
    }

    @PatchMapping("/customers/{id}/loyalty")
    public LoyaltyUpdateResponse updateLoyalty(
            @PathVariable @Positive Long id,
            @Valid @RequestBody LoyaltyUpdateRequest request
    ) {
        return retailService.updateLoyalty(id, request);
    }
}
