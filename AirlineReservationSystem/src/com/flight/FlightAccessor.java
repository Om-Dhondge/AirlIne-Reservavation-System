package com.flight;

interface FlightAccessor{
        String getFlightId();
        String getOrigin();
        String getDestination();
        boolean isInternational();
        int getTotalSeats();
        int getAvailableSeats();
        double getBaseFare();
        String getFlightDate();
    }
    