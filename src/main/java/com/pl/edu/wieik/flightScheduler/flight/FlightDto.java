package com.pl.edu.wieik.flightScheduler.flight;

import lombok.Data;

import java.time.Instant;

@Data
public class FlightDto {
    private String flightNumber;
    private String destination;
    private Instant firstSeen;
    private Instant plannedArrival;
    private Instant plannedDeparture;
}
