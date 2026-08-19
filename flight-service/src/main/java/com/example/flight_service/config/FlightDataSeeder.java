package com.example.flight_service.config;

import com.example.flight_service.model.Flight;
import com.example.flight_service.repository.FlightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds the flights table with a realistic sample schedule on startup so
 * there's something to find when searching. Only runs when the table is
 * empty, so it's safe to restart the service without creating duplicates
 * or clobbering flights created/edited afterwards.
 *
 * Departure dates are calculated relative to "today" (the day the service
 * starts), so the sample schedule always looks current instead of slowly
 * drifting into the past.
 */
@Component
public class FlightDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(FlightDataSeeder.class);

    private final FlightRepository flightRepository;

    public FlightDataSeeder(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Override
    public void run(String... args) {
        if (flightRepository.count() > 0) {
            log.info("Flights table already has data — skipping seed.");
            return;
        }

        LocalDate today = LocalDate.now();
        List<Flight> flights = new ArrayList<>();

        // route, flightNumber, hour:min departure, duration hours, price, seats, status, daysFromToday
        Object[][] rows = {
            // CMB <-> DXB (matches the example on the search page)
            {"CMB", "DXB", "UL225", 8, 0, 4, 320.50, 180, "SCHEDULED", 1},
            {"CMB", "DXB", "UL227", 14, 30, 4, 289.00, 42, "SCHEDULED", 1},
            {"CMB", "DXB", "EK655", 21, 15, 4, 410.75, 6, "SCHEDULED", 2},
            {"CMB", "DXB", "UL225", 8, 0, 4, 335.00, 180, "SCHEDULED", 3},
            {"CMB", "DXB", "UL229", 5, 45, 4, 265.25, 0, "SCHEDULED", 4},
            {"CMB", "DXB", "EK656", 12, 10, 4, 355.00, 90, "DELAYED", 5},
            {"DXB", "CMB", "UL226", 15, 40, 4, 300.00, 165, "SCHEDULED", 1},
            {"DXB", "CMB", "EK654", 23, 55, 4, 398.50, 12, "SCHEDULED", 2},
            {"DXB", "CMB", "UL228", 9, 20, 4, 275.00, 120, "CANCELLED", 3},

            // CMB <-> SIN
            {"CMB", "SIN", "UL301", 6, 15, 4, 410.00, 200, "SCHEDULED", 1},
            {"CMB", "SIN", "SQ469", 13, 0, 4, 455.90, 30, "SCHEDULED", 2},
            {"CMB", "SIN", "UL303", 19, 30, 4, 389.00, 8, "SCHEDULED", 4},
            {"SIN", "CMB", "UL302", 22, 45, 4, 415.00, 210, "SCHEDULED", 1},
            {"SIN", "CMB", "SQ470", 7, 10, 4, 470.25, 55, "DELAYED", 3},

            // CMB <-> LHR
            {"CMB", "LHR", "UL501", 1, 30, 11, 690.00, 240, "SCHEDULED", 2},
            {"CMB", "LHR", "BA091", 23, 45, 11, 745.50, 18, "SCHEDULED", 5},
            {"LHR", "CMB", "UL502", 10, 20, 11, 705.00, 230, "SCHEDULED", 3},
            {"LHR", "CMB", "BA090", 20, 0, 11, 760.00, 4, "SCHEDULED", 6},

            // DXB <-> LHR
            {"DXB", "LHR", "EK001", 8, 45, 7, 520.00, 300, "SCHEDULED", 1},
            {"DXB", "LHR", "EK003", 20, 15, 7, 545.75, 95, "SCHEDULED", 2},
            {"LHR", "DXB", "EK002", 14, 0, 7, 530.00, 280, "SCHEDULED", 1},

            // LHR <-> JFK
            {"LHR", "JFK", "BA177", 11, 0, 8, 610.00, 250, "SCHEDULED", 3},
            {"LHR", "JFK", "VS003", 17, 30, 8, 585.25, 60, "SCHEDULED", 4},
            {"JFK", "LHR", "BA178", 21, 40, 8, 615.00, 190, "SCHEDULED", 3},

            // SIN <-> BKK
            {"SIN", "BKK", "TG403", 9, 0, 2, 145.00, 150, "SCHEDULED", 1},
            {"SIN", "BKK", "SQ978", 16, 45, 2, 160.50, 20, "SCHEDULED", 2},
            {"BKK", "SIN", "TG404", 12, 30, 2, 150.00, 140, "SCHEDULED", 1},

            // CMB <-> BKK
            {"CMB", "BKK", "UL401", 4, 20, 3, 210.00, 175, "SCHEDULED", 2},
            {"BKK", "CMB", "UL402", 18, 10, 3, 220.00, 160, "SCHEDULED", 2},

            // DXB <-> SIN
            {"DXB", "SIN", "EK354", 3, 0, 7, 480.00, 260, "SCHEDULED", 4},
            {"SIN", "DXB", "EK355", 15, 20, 7, 495.00, 100, "SCHEDULED", 5},
        };

        for (Object[] row : rows) {
            String origin = (String) row[0];
            String destination = (String) row[1];
            String flightNumber = (String) row[2];
            int hour = (int) row[3];
            int minute = (int) row[4];
            int durationHours = (int) row[5];
            double price = (double) row[6];
            int seats = (int) row[7];
            String status = (String) row[8];
            int daysFromToday = (int) row[9];

            LocalDateTime departure = LocalDateTime.of(today.plusDays(daysFromToday), LocalTime.of(hour, minute));
            LocalDateTime arrival = departure.plusHours(durationHours);

            Flight flight = new Flight();
            flight.setFlightNumber(flightNumber);
            flight.setOrigin(origin);
            flight.setDestination(destination);
            flight.setDepartureTime(departure);
            flight.setArrivalTime(arrival);
            flight.setStatus(status);
            flight.setSeatsAvailable(seats);
            flight.setPrice(price);
            flights.add(flight);
        }

        flightRepository.saveAll(flights);
        log.info("Seeded {} sample flights.", flights.size());
    }
}
