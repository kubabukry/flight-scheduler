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

                // Parse the planned arrival time and convert it to an Instant
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime localTime = LocalTime.parse(values[0].replace("\"", ""), formatter);
                Instant plannedArrival = currentDate
                        .atTime(localTime)
                        .atZone(ZoneId.systemDefault())
                        .toInstant();

                String flightNumber = values[2].replace("\"", "");

                // Generate a random number between -10 and 10
                int[] minutesToAdd = {30, 60, 120};
                Random random = new Random();
                int randomFirstSeenMinutes = random.nextInt(21) - 10;

                // Add the random number of minutes to the plannedArrival time to get the firstSeen time
                Instant firstSeen = plannedArrival.plus(randomFirstSeenMinutes, ChronoUnit.MINUTES);

                // If the firstSeen time is in the past, set isActive to false
                boolean isActive = !firstSeen.isBefore(Instant.now());

                // Check if a flight with the same number already exists
                Flight existingFlight = flightRepository.findByFlightNumber(flightNumber);

                // If the flight already exists, update its isActive status
                if (existingFlight != null) {
                    existingFlight.setIsActive(isActive);
                    flightRepository.save(existingFlight);
                }

                // Check if the planned arrival time is in the future
                if (plannedArrival.isAfter(Instant.now())) {
                    // If the flight does not exist, create and save it
                    if (existingFlight == null) {
                        Flight flight = new Flight();
                        flight.setPlannedArrival(plannedArrival);
                        flight.setDestination(values[1].replace("\"", ""));
                        flight.setFlightNumber(flightNumber);
                        flight.setIsActive(isActive);
                        flight.setStatus(Status.ARRIVAL);

                        // Generate a random number (30, 60, or 120)
                        int randomMinutes = minutesToAdd[random.nextInt(minutesToAdd.length)];

                        // Add the random number of minutes to the plannedArrival time to get the plannedDeparture time
                        Instant plannedDeparture = plannedArrival.plus(randomMinutes, ChronoUnit.MINUTES);
                        flight.setPlannedDeparture(plannedDeparture);

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
}
