package com.fms.billing_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processPayment(@RequestBody Map<String, Object> paymentDetails) {
        String cardNumber = (String) paymentDetails.get("cardNumber");
        
        Map<String, Object> response = new HashMap<>();
        
        // Fake validation: If the card exists and is at least 12 digits, approve it!
        if (cardNumber != null && cardNumber.length() >= 12) {
            response.put("status", "SUCCESS");
            response.put("transactionId", "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            response.put("message", "Payment processed successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "FAILED");
            response.put("message", "Invalid card details");
            return ResponseEntity.badRequest().body(response);
        }
    }
}