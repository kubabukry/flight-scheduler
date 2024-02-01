package com.pl.edu.wieik.flightScheduler.flight;

import java.util.List;
import java.util.stream.Collectors;

public class FlightMapper {

    private static FlightDto mapFlightToFlightDto(Flight flight){
        FlightDto flightDto = new FlightDto();
        flightDto.setFlightNumber(flight.getFlightNumber());
        flightDto.setDestination(flight.getDestination());
        flightDto.setFirstSeen(flight.getFirstSeen());
        flightDto.setPlannedArrival(flight.getPlannedArrival());
        flightDto.setPlannedDeparture(flight.getPlannedDeparture());
        return flightDto;
    }

    public static List<FlightDto> mapFlightListToFlightDtoList(List<Flight> flightList){
        return flightList.stream()
                .map(FlightMapper::mapFlightToFlightDto)
                .collect(Collectors.toList());
    }
}
