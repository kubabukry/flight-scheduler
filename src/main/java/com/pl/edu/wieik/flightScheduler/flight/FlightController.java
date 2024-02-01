package com.pl.edu.wieik.flightScheduler.flight;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/flight/all")
    public List<FlightDto> getFlightList(){
        return flightService.getFlightList();
    }
}
