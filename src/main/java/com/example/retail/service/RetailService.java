package com.example.retail.service;

import com.example.retail.config.TestDataLoader;
import com.example.retail.dto.*;
import com.example.retail.exception.BusinessRuleException;
import com.example.retail.exception.ResourceNotFoundException;
import com.example.retail.model.Customer;
import com.example.retail.model.CustomerOrder;
import com.example.retail.model.Offer;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RetailService {

    private final Map<Long, Customer> customers = new ConcurrentHashMap<>();
    private final Map<Long, CustomerOrder> orders = new ConcurrentHashMap<>();
    private final Map<String, Offer> offers = new ConcurrentHashMap<>();

    public RetailService(TestDataLoader dataLoader) {
        dataLoader.loadCustomers().forEach(c -> customers.put(c.getId(), c));
        dataLoader.loadOrders().forEach(o -> orders.put(o.getId(), o));
        dataLoader.loadOffers().forEach(of -> offers.put(of.getCode(), of));
    }

    public List<Customer> getAllCustomers() {
        return customers.values()
                .stream()
                .sorted(Comparator.comparing(Customer::getId))
                .toList();
    }

    public CustomerProfileResponse getCustomerProfile(Long id) {
        Customer customer = findCustomer(id);

        List<OrderResponse> orderHistory = orders.values()
                .stream()
                .filter(order -> order.getCustomerId().equals(id))
                .sorted(Comparator.comparing(CustomerOrder::getId))
                .map(this::toOrderResponse)
                .toList();

        return new CustomerProfileResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getLoyaltyPoints(),
                customer.isActive(),
                customer.getJoinedDate(),
                orderHistory
        );
    }

    public OfferResponse getOffer(String code) {
        Offer offer = offers.get(code.toUpperCase());

        if (offer == null) {
            throw new ResourceNotFoundException("Offer not found for code: " + code);
        }

        return new OfferResponse(
                offer.getCode(),
                offer.getDiscountPercent(),
                offer.isActive()
        );
    }

    public LoyaltyUpdateResponse updateLoyalty(Long customerId, LoyaltyUpdateRequest request) {
        Customer customer = findCustomer(customerId);

        int previous = customer.getLoyaltyPoints();
        int updated = previous + request.pointsDelta();

        if (updated < 0) {
            throw new BusinessRuleException("Loyalty points cannot become negative");
        }

        customer.setLoyaltyPoints(updated);

        return new LoyaltyUpdateResponse(
                customer.getId(),
                previous,
                updated,
                request.pointsDelta(),
                request.reason()
        );
    }

    private Customer findCustomer(Long id) {
        Customer customer = customers.get(id);

        if (customer == null) {
            throw new ResourceNotFoundException("Customer not found for id: " + id);
        }

        return customer;
    }

    private OrderResponse toOrderResponse(CustomerOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getTotalAmount(),
                order.getStatus()
        );
    }

}
