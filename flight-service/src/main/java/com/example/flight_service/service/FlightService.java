package com.example.flight_service.service;

import com.example.flight_service.model.Flight;
import com.example.flight_service.repository.FlightRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FlightService {

    @Autowired
    private FlightRepository flightRepository;

    public List<Flight> getAllFlights() { return flightRepository.findAll(); }
    public Optional<Flight> getFlightById(Long id) { return flightRepository.findById(id); }
    public Flight createFlight(Flight flight) { return flightRepository.save(flight); }
    public Flight updateFlight(Long id, Flight flight) { 
        flight.setId(id); 
        return flightRepository.save(flight); 
    }
    public void deleteFlight(Long id) { flightRepository.deleteById(id); }

    // DYNAMIC ADVANCED SEARCH
    public List<Flight> searchFlights(String origin, String destination, LocalDate date, 
                                      Double maxPrice, String status, String sortBy, String sortDir) {
        
        Specification<Flight> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (origin != null && !origin.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("origin"), origin.trim()));
            }
            if (destination != null && !destination.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("destination"), destination.trim()));
            }
            if (date != null) {
                // Translates the HTML YYYY-MM-DD into a full 24-hour range
                LocalDateTime startOfDay = date.atStartOfDay();
                LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
                predicates.add(criteriaBuilder.between(root.get("departureTime"), startOfDay, endOfDay));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            if (status != null && !status.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status.trim()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // Handle Sorting direction
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, (sortBy != null && !sortBy.trim().isEmpty()) ? sortBy : "departureTime");

        return flightRepository.findAll(spec, sort);
    }
}