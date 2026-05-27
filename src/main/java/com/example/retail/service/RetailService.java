package com.example.retail.service;

import com.example.retail.dto.*;
import com.example.retail.exception.BusinessRuleException;
import com.example.retail.exception.DuplicateResourceException;
import com.example.retail.exception.ResourceNotFoundException;
import com.example.retail.model.Customer;
import com.example.retail.model.CustomerOrder;
import com.example.retail.model.Offer;
import com.example.retail.repository.CustomerOrderRepository;
import com.example.retail.repository.CustomerRepository;
import com.example.retail.repository.OfferRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)   // default: read-only for all methods
public class RetailService {

    private final CustomerRepository      customerRepository;
    private final CustomerOrderRepository orderRepository;
    private final OfferRepository         offerRepository;

    public RetailService(CustomerRepository customerRepository,
                         CustomerOrderRepository orderRepository,
                         OfferRepository offerRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository    = orderRepository;
        this.offerRepository    = offerRepository;
    }

    // ── Customers ────────────────────────────────────────────────────────────

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll(Sort.by("id"));
    }

    public CustomerProfileResponse getCustomerProfile(Long id) {
        Customer customer = findCustomer(id);

        List<OrderResponse> orderHistory = orderRepository
                .findByCustomerIdOrderByIdAsc(id)
                .stream()
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

    // ── Offers ───────────────────────────────────────────────────────────────

    public List<OfferResponse> getAllOffers() {
        return offerRepository.findAll(Sort.by("code"))
                .stream()
                .map(this::toOfferResponse)
                .toList();
    }

    public OfferResponse getOffer(String code) {
        return toOfferResponse(findOffer(code));
    }

    @Transactional
    public OfferResponse createOffer(CreateOfferRequest request) {
        String code = request.code().toUpperCase();

        if (offerRepository.existsById(code)) {
            throw new DuplicateResourceException(
                    "Offer already exists with code: " + code);
        }

        Offer offer = new Offer(code, request.discountPercent(), request.active());
        offerRepository.save(offer);
        return toOfferResponse(offer);
    }

    @Transactional
    public OfferResponse updateOffer(String code, UpdateOfferRequest request) {
        Offer offer = findOffer(code);
        offer.setDiscountPercent(request.discountPercent());
        offer.setActive(request.active());
        offerRepository.save(offer);
        return toOfferResponse(offer);
    }

    @Transactional
    public void deleteOffer(String code) {
        findOffer(code);           // throws 404 if not found
        offerRepository.deleteById(code.toUpperCase());
    }

    // ── Loyalty ──────────────────────────────────────────────────────────────

    @Transactional          // override: write operation
    public LoyaltyUpdateResponse updateLoyalty(Long customerId, LoyaltyUpdateRequest request) {
        Customer customer = findCustomer(customerId);

        int previous = customer.getLoyaltyPoints();
        int updated  = previous + request.pointsDelta();

        if (updated < 0) {
            throw new BusinessRuleException("Loyalty points cannot become negative");
        }

        customer.setLoyaltyPoints(updated);
        customerRepository.save(customer);   // explicit save for clarity

        return new LoyaltyUpdateResponse(
                customer.getId(),
                previous,
                updated,
                request.pointsDelta(),
                request.reason()
        );
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Customer findCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found for id: " + id));
    }

    private Offer findOffer(String code) {
        return offerRepository.findById(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Offer not found for code: " + code));
    }

    private OrderResponse toOrderResponse(CustomerOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getTotalAmount(),
                order.getStatus()
        );
    }

    private OfferResponse toOfferResponse(Offer offer) {
        return new OfferResponse(
                offer.getCode(),
                offer.getDiscountPercent(),
                offer.isActive()
        );
    }
}
