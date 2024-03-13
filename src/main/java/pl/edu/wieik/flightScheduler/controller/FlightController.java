package pl.edu.wieik.flightScheduler.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    @GetMapping("/flights")
    @ResponseStatus(value = HttpStatus.OK)
    public List<FlightDto> getFlightList(){
        return flightService.getFlightList();
    }
}
