package com.pl.edu.wieik.flightScheduler.flight;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Service
public class FlightService {
    private final FlightRepository flightRepository;
    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public void populateDatabaseFromCSV() {
        String path = "data.csv";
        String line = "";

        LocalDate currentDate = LocalDate.now();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                // Parse planned arrival time and convert to an Instant
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime localTime = LocalTime.parse(values[0].replace("\"", ""), formatter);
                Instant plannedArrival = currentDate
                        .atTime(localTime)
                        .atZone(ZoneId.systemDefault())
                        .toInstant();

                String flightNumber = values[2].replace("\"", "");

                // Generate random times for plannedDeparture and delay/early arrival
                int[] minutesToAdd = {70, 90, 120};
                Random random = new Random();
                int randomFirstSeenMinutes = random.nextInt(41) - 20;

                // Add the random number of minutes to the plannedArrival time to get the firstSeen time
                Instant firstSeen = plannedArrival.plus(randomFirstSeenMinutes, ChronoUnit.MINUTES);

                // Check if a flight with the same number already exists
                Flight existingFlight = flightRepository.findByFlightNumber(flightNumber);

                // Check if the planned arrival time is in the future
                if (plannedArrival.isAfter(Instant.now())) {
                    // If the flight does not exist, create and save it
                    if (existingFlight == null) {
                        Flight flight = new Flight();
                        flight.setPlannedArrival(plannedArrival);
                        flight.setDestination(values[1].replace("\"", ""));
                        flight.setFlightNumber(flightNumber);

                        //pick random interval for plannedDeparture
                        int randomMinutes = minutesToAdd[random.nextInt(minutesToAdd.length)];

                        // Add the random interval to plannedArrival time to get plannedDeparture
                        Instant plannedDeparture = plannedArrival.plus(randomMinutes, ChronoUnit.MINUTES);
                        flight.setPlannedDeparture(plannedDeparture);

                        //set firstSeen with early/delay arrival
                        flight.setFirstSeen(firstSeen);

                        // Save the Flight object to the database
                        flightRepository.save(flight);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllFlights() {
        flightRepository.deleteAll();
    }

    public List<FlightDto> getFlightList() {
        return FlightMapper.mapFlightListToFlightDtoList(flightRepository.findAllByFirstSeenBefore(Instant.now()));
    }
}
