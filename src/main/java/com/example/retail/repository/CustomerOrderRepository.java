package com.example.retail.repository;

import com.example.retail.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    /** Returns all orders for a given customer, sorted by id ascending. */
    List<CustomerOrder> findByCustomerIdOrderByIdAsc(Long customerId);
}
