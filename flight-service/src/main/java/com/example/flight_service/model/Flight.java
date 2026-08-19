package com.example.flight_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "flights")
@Data
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // No annotations needed for these strings!
    private String flightNumber;
    private String origin;
    private String destination;
    
    // The date format annotations belong here:
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime departureTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime arrivalTime;

    private String status;        // SCHEDULED, DELAYED, CANCELLED
    private Integer seatsAvailable;
    private Double price;
}