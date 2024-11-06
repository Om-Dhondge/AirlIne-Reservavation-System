package com.manager;

import com.flight.Flight;
import java.text.ParseException;
import java.util.List;

public class Manager implements ManagerAccessories{
    private String name;
    private String password;

    public Manager(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }

    public void addFlight(List<Flight> flights, String flightId, String origin, String destination,
                          boolean isInternational, int totalSeats, double baseFare, String date) throws ParseException {
        Flight newFlight = new Flight(flightId, origin, destination, isInternational, totalSeats, baseFare, date);
        flights.add(newFlight);
    }
}


    

