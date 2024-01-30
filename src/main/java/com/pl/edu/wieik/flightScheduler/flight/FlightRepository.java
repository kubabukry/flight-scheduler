package com.pl.edu.wieik.flightScheduler.flight;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    Flight findByFlightNumber(String flightNumber);

    List<Flight> findAllByFirstSeenBefore(Instant before);

    @Query("SELECT f FROM Flight f ORDER BY f.firstSeen ASC")
    List<Flight> findAllOrderByFirstSeenAsc();

    default Flight findEarliestFirstSeen() {
        List<Flight> flights = findAllOrderByFirstSeenAsc();
        return flights.isEmpty() ? null : flights.get(0);
    }
}
