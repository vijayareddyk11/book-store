package com.sporty.book.store.service;

import com.sporty.book.store.entities.Customer;
import com.sporty.book.store.mappers.CustomerMapper;
import com.sporty.book.store.repositories.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.openapitools.model.LoyaltyPointsDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    private static final int POINTS_NEEDED_FOR_FREE_BOOK = 10;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public LoyaltyPointsDTO getLoyaltyPoints(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + customerId));
        return customerMapper.toLoyaltyPointsDto(customer);
    }

    @Transactional
    public void addLoyaltyPoints(UUID customerId, int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Points cannot be negative");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + customerId));

        int currentPoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
        customer.setLoyaltyPoints(currentPoints + points);
        customerRepository.save(customer);
    }

    @Transactional
    public void useLoyaltyPoints(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + customerId));

        if (customer.getLoyaltyPoints() < POINTS_NEEDED_FOR_FREE_BOOK) {
            throw new IllegalStateException(
                    "Insufficient loyalty points. Needed: " + POINTS_NEEDED_FOR_FREE_BOOK +
                            ", Available: " + customer.getLoyaltyPoints()
            );
        }

        customer.setLoyaltyPoints(0);
        customerRepository.save(customer);
    }


    public boolean canRedeemFreeBook(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + customerId));
        return customer.getLoyaltyPoints() >= POINTS_NEEDED_FOR_FREE_BOOK;
    }
}
