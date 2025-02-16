package com.sporty.book.store.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.sporty.book.store.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.LoyaltyPointsDTO;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class LoyalityApiControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private LoyalityApiController loyalityApiController;

    private UUID customerId;
    private LoyaltyPointsDTO loyaltyPointsDTO;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        loyaltyPointsDTO = new LoyaltyPointsDTO();
        loyaltyPointsDTO.setCustomerId(customerId);
        loyaltyPointsDTO.setPoints(100);
    }

    @Test
    void testGetLoyaltyPoints_Success() {

        when(customerService.getLoyaltyPoints(customerId)).thenReturn(loyaltyPointsDTO);

        ResponseEntity<LoyaltyPointsDTO> response = loyalityApiController.getLoyaltyPoints(customerId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(customerId, response.getBody().getCustomerId());
        assertEquals(100, response.getBody().getPoints());
    }

}
