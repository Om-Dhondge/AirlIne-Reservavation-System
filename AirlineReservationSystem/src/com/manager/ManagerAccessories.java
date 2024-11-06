package com.manager;

import com.flight.*;
import java.util.List;
import java.text.ParseException;

interface ManagerAccessories{
    boolean validatePassword(String inputPassword);
    void addFlight(List<Flight> flights, String flightId, String origin, String destination,
                   boolean isInternational, int totalSeats, double baseFare, String date) throws ParseException;

}