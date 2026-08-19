package com.fms.notification_service.controller;

import com.fms.notification_service.dto.EmailRequest;
import com.fms.notification_service.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/booking")
    public ResponseEntity<String> sendConfirmation(@RequestBody EmailRequest request) {
        try {
            emailService.sendBookingConfirmation(
                    request.getEmail(),
                    request.getName(),
                    request.getFlightId(),
                    request.getSeatNumber()
            );
            return ResponseEntity.ok("Email sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to send email");
        }
    }
}