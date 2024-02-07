package pl.edu.wieik.flightScheduler.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.wieik.flightScheduler.dto.FlightDto;
import pl.edu.wieik.flightScheduler.service.FlightService;

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
