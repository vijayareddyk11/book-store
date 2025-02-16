package com.sporty.book.store.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.sporty.book.store.service.PurchaseService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.PurchaseRequest;
import org.openapitools.model.PurchaseResponse;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class PurchaseApiControllerTest {

    @Mock
    private PurchaseService purchaseService;

    @InjectMocks
    private PurchaseApiController purchaseApiController;

    private UUID customerId;
    private PurchaseRequest purchaseRequest;
    private PurchaseResponse purchaseResponse;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        purchaseRequest = new PurchaseRequest();

        purchaseRequest.setCustomerId(customerId);

        purchaseResponse = new PurchaseResponse();
        purchaseResponse.setPurchaseId(UUID.randomUUID());
        purchaseResponse.setTotal(100.0);
    }

    @Test
    void testPurchaseBooks_Success() {

        when(purchaseService.createPurchase(purchaseRequest)).thenReturn(purchaseResponse);

        ResponseEntity<PurchaseResponse> response = purchaseApiController.purchaseBooks(purchaseRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(purchaseResponse.getPurchaseId(), response.getBody().getPurchaseId());
        assertEquals(purchaseResponse.getTotal(), response.getBody().getTotal());
    }

}
