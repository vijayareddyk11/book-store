package com.sporty.book.store.service;

import com.sporty.book.store.entities.Customer;
import com.sporty.book.store.mappers.CustomerMapper;
import com.sporty.book.store.repositories.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.LoyaltyPointsDTO;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    private static final int POINTS_NEEDED_FOR_FREE_BOOK = 10;

    @Test
    void getLoyaltyPoints_ShouldReturnLoyaltyPointsDTO_WhenCustomerExists() {
        UUID customerId = UUID.randomUUID();
        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);
        mockCustomer.setLoyaltyPoints(100);

        LoyaltyPointsDTO expectedDto = new LoyaltyPointsDTO();
        expectedDto.setPoints(100);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(customerMapper.toLoyaltyPointsDto(mockCustomer)).thenReturn(expectedDto);

        LoyaltyPointsDTO result = customerService.getLoyaltyPoints(customerId);

        assertNotNull(result);
        assertEquals(100, result.getPoints());
        verify(customerRepository).findById(customerId);
        verify(customerMapper).toLoyaltyPointsDto(mockCustomer);
    }

    @Test
    void getLoyaltyPoints_ShouldThrowException_WhenCustomerNotFound() {
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> customerService.getLoyaltyPoints(customerId)
        );

        assertEquals("Customer not found with id: " + customerId, exception.getMessage());
        verify(customerRepository).findById(customerId);
        verifyNoInteractions(customerMapper);
    }

    @Test
    void addLoyaltyPoints_ShouldIncreasePoints_WhenCustomerExists() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        int pointsToAdd = 50;

        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);
        mockCustomer.setLoyaltyPoints(100);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));

        customerService.addLoyaltyPoints(customerId, pointsToAdd);

        assertEquals(150, mockCustomer.getLoyaltyPoints());
        verify(customerRepository).findById(customerId);
        verify(customerRepository).save(mockCustomer);
    }

    @Test
    void addLoyaltyPoints_ShouldThrowException_WhenCustomerNotFound() {
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> customerService.addLoyaltyPoints(customerId, 50)
        );

        assertEquals("Customer not found with id: " + customerId, exception.getMessage());
        verify(customerRepository).findById(customerId);
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    void addLoyaltyPoints_ShouldThrowException_WhenPointsNegative() {
        UUID customerId = UUID.randomUUID();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.addLoyaltyPoints(customerId, -10)
        );

        assertEquals("Points cannot be negative", exception.getMessage());
        verifyNoInteractions(customerRepository);
    }

    @Test
    void useLoyaltyPoints_ShouldResetPoints_WhenCustomerHasEnoughPoints() {
        UUID customerId = UUID.randomUUID();
        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);
        mockCustomer.setLoyaltyPoints(150); // Customer has enough points

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));

        customerService.useLoyaltyPoints(customerId);

        assertEquals(0, mockCustomer.getLoyaltyPoints());
        verify(customerRepository).findById(customerId);
        verify(customerRepository).save(mockCustomer);
    }

    @Test
    void useLoyaltyPoints_ShouldThrowException_WhenCustomerNotFound() {
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> customerService.useLoyaltyPoints(customerId)
        );

        assertEquals("Customer not found with id: " + customerId, exception.getMessage());
        verify(customerRepository).findById(customerId);
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    void useLoyaltyPoints_ShouldThrowException_WhenPointsAreInsufficient() {

        UUID customerId = UUID.randomUUID();
        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);
        mockCustomer.setLoyaltyPoints(8);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> customerService.useLoyaltyPoints(customerId)
        );

        assertEquals(
                "Insufficient loyalty points. Needed: " + POINTS_NEEDED_FOR_FREE_BOOK + ", Available: 8",
                exception.getMessage()
        );

        verify(customerRepository).findById(customerId);
        verifyNoMoreInteractions(customerRepository);
    }
}