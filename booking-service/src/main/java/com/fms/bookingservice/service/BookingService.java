package com.fms.bookingservice.service;

import com.fms.bookingservice.model.Booking;
import com.fms.bookingservice.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Booking createBooking(Booking booking, String cardNumber) {
        
        // 1. Save the booking to the database
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("CONFIRMED");
        Booking savedBooking = bookingRepository.save(booking);

        // 2. Ask the User Service for the passenger's email
        String passengerEmail = ""; 
        try {
            HttpHeaders userHeaders = new HttpHeaders();
            userHeaders.set("X-API-KEY", "USER-SERVICE-SECRET-KEY-2026"); 
            HttpEntity<String> userEntity = new HttpEntity<>(userHeaders);

            String userServiceUrl = "http://user-service:8083/api/users/" + booking.getUserId();
            
            org.springframework.http.ResponseEntity<Map<String, Object>> userResponse = restTemplate.exchange(
                    userServiceUrl, 
                    org.springframework.http.HttpMethod.GET, 
                    userEntity, 
                    new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (userResponse.getBody() != null && userResponse.getBody().get("email") != null) {
                passengerEmail = (String) userResponse.getBody().get("email");
                System.out.println("Successfully fetched email from User Service: " + passengerEmail);
            }
        } catch (Exception e) {
            System.out.println("Failed to fetch user email: " + e.getMessage());
        }

        // 3. Trigger the Notification Service
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-API-KEY", "NOTIFICATION-SERVICE-SECRET-KEY-2026");

            Map<String, Object> emailRequest = new HashMap<>();
            
            emailRequest.put("email", passengerEmail); 
            emailRequest.put("name", booking.getPassengerName());
            emailRequest.put("flightId", String.valueOf(booking.getFlightId()));
            emailRequest.put("seatNumber", booking.getSeatNumber());

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(emailRequest, headers);
            
            restTemplate.postForEntity("http://notification-service:8084/api/notifications/booking", request, String.class);
            System.out.println("Notification trigger sent successfully to " + passengerEmail + "!");
            
        } catch (Exception e) {
            System.out.println("Failed to trigger notification: " + e.getMessage());
        }

        return savedBooking;
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public Booking cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        
        booking.setStatus("CANCELLED");
        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}