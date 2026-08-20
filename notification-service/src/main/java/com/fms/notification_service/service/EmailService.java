package com.fms.notification_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendBookingConfirmation(String toEmail, String name, String flightId, String seatNumber) {
        SimpleMailMessage message = new SimpleMailMessage();
        
        message.setTo(toEmail);
        message.setSubject("Flight Booking Confirmed! ✈️");
        message.setText("Hello " + name + ",\n\n"
                + "Your payment was successful and your booking is confirmed!\n\n"
                + "Flight ID: " + flightId + "\n"
                + "Seat Number: " + seatNumber + "\n\n"
                + "Thank you for flying with us. Have a great trip!");
                
        mailSender.send(message);
    }
}