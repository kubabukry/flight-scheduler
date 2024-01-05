package com.pl.edu.wieik.flightScheduler.configuration;

import com.pl.edu.wieik.flightScheduler.flight.FlightService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {

    private final FlightService flightService;

    public DataLoader(FlightService flightService) {
        this.flightService = flightService;
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        flightService.populateDatabaseFromCSV();
    }
}
