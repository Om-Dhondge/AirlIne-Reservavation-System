package com.flight;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FlightStatistics {
    public static Map<String, Long> countFlightsByOrigin(List<Flight> flights) {
        return flights.stream()
                .collect(Collectors.groupingBy(Flight::getOrigin, Collectors.counting()));
    }

    public static double calculateAverageFare(List<Flight> flights) {
        return flights.stream()
                .mapToDouble(Flight::getBaseFare)
                .average()
                .orElse(0.0);
    }

    public static int countAvailableSeats(List<Flight> flights) {
        return flights.stream()
                .mapToInt(Flight::getAvailableSeats)
                .sum();
    }

    // New method to count the number of flights with at least one booked seat
    public static long countBookedFlights(List<Flight> flights) {
        return flights.stream()
                .filter(flight -> flight.getTotalSeats() > flight.getAvailableSeats())
                .count();
    }
}
