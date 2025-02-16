package com.sporty.book.store.controller;

import com.sporty.book.store.service.PurchaseService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.openapitools.api.PurchasesApi;
import org.openapitools.model.PurchaseRequest;
import org.openapitools.model.PurchaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PurchaseApiController  implements PurchasesApi {

    @Autowired
    private PurchaseService purchaseService;

    @Override
    public ResponseEntity<PurchaseResponse> purchaseBooks(@Valid @RequestBody PurchaseRequest purchaseRequest){

        PurchaseResponse purchase = purchaseService.createPurchase(purchaseRequest);
        return ResponseEntity.ok(purchase);
    }

}
