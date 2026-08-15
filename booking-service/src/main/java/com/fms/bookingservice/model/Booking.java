package com.fms.bookingservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long flightId;
    private Long userId;
    private String passengerName;
    private String seatNumber;
    private LocalDateTime bookingDate;
    private String status;   // CONFIRMED, CANCELLED
}