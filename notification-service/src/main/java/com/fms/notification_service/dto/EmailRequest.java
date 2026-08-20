package com.fms.notification_service.dto;

import lombok.Data;

@Data
public class EmailRequest {
    private String email;
    private String name;
    private String flightId;
    private String seatNumber;
}