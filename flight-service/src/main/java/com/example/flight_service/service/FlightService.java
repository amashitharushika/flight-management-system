package com.example.flight_service.service;

import com.example.flight_service.model.Flight;
import com.example.flight_service.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FlightService {

    @Autowired
    private FlightRepository flightRepository;

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Optional<Flight> getFlightById(Long id) {
        return flightRepository.findById(id);
    }

    public Flight createFlight(Flight flight) {
        return flightRepository.save(flight);
    }

    public Flight updateFlight(Long id, Flight updated) {
        if (!flightRepository.existsById(id)) {
        throw new RuntimeException("Flight not found with id: " + id);
    }
        updated.setId(id);
        return flightRepository.save(updated);
    }

    public void deleteFlight(Long id) {
        flightRepository.deleteById(id);
    }

    public List<Flight> searchFlights(String origin, String destination) {
        return flightRepository.findByOriginAndDestination(origin, destination);
    }
}
