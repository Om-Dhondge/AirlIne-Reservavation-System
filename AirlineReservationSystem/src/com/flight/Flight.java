package com.flight;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Flight implements FlightAccessor {
    private String flightId;
    private String origin;
    private String destination;
    private boolean isInternational;
    private int totalSeats;
    private int availableSeats;
    private double baseFare;
    private Date flightDate;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private static final double SEAT_SCARCITY_FACTOR = 0.5; // 50% increase when few seats left
    private static final double LAST_MINUTE_FACTOR = 0.4; // 40% increase for last-minute bookings
    private static final int LAST_MINUTE_DAYS = 7; // Define last-minute as 7 days before flight

    public Flight(String flightId, String origin, String destination, boolean isInternational,
                  int totalSeats, double baseFare, String date) throws ParseException {
        this.flightId = flightId;
        this.origin = origin;
        this.destination = destination;
        this.isInternational = isInternational;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.baseFare = baseFare;
        this.flightDate = dateFormat.parse(date);
    }

    public String getFlightId() { return flightId; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public boolean isInternational() { return isInternational; }
    public int getTotalSeats() { return totalSeats; }
    public int getAvailableSeats() { return availableSeats; }
    public double getBaseFare() { return baseFare; }
    public String getFlightDate() { return dateFormat.format(flightDate); }

    public boolean isFullCapacity() { return availableSeats == 0; }

    public void bookSeats(int seats) {
        if (availableSeats >= seats) {
            availableSeats -= seats;
        } else {
            throw new IllegalArgumentException("Not enough available seats.");
        }
    }

    public double calculateDynamicFare() {
        double dynamicFare = baseFare;

        // Adjust fare based on availability
        if (availableSeats < totalSeats * 0.2) { // If less than 20% seats available
            dynamicFare *= (1 + SEAT_SCARCITY_FACTOR);
        }

        // Adjust fare for last-minute bookings
        long daysUntilFlight = (flightDate.getTime() - System.currentTimeMillis()) / (1000 * 60 * 60 * 24);
        if (daysUntilFlight < LAST_MINUTE_DAYS) {
            dynamicFare *= (1 + LAST_MINUTE_FACTOR);
        }

        return dynamicFare;
    }

    public String getPriceBreakdown() {
        double totalFare = calculateDynamicFare();
        return String.format("Total Fare: $%.2f", totalFare);
    }

}