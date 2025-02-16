package com.sporty.book.store.controller;

import com.sporty.book.store.service.CustomerService;
import org.openapitools.api.LoyaltyApi;
import org.openapitools.model.LoyaltyPointsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class LoyalityApiController implements LoyaltyApi {

    @Autowired
    CustomerService customerService;

    @Override
    public ResponseEntity<LoyaltyPointsDTO> getLoyaltyPoints(@PathVariable("customerId")UUID customerId) {
        LoyaltyPointsDTO loyaltyPoints = customerService.getLoyaltyPoints(customerId);
        return new ResponseEntity<>(loyaltyPoints, HttpStatus.OK);


    }
}
