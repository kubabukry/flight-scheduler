package com.pl.edu.wieik.flightScheduler.flight;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    Flight findByFlightNumber(String flightNumber);

    @Query("SELECT f FROM Flight f ORDER BY f.firstSeen ASC")
    List<Flight> findAllOrderByFirstSeenAsc();

    @Query("SELECT f FROM Flight f WHERE f.firstSeen < :before ORDER BY f.firstSeen ASC")
    List<Flight> findAllByFirstSeenBefore(@Param("before") Instant before);

    @Query("SELECT f FROM Flight f WHERE f.firstSeen > :past ORDER BY f.firstSeen ASC")
    List<Flight> findAllFlightsPastNow(@Param("past") Instant now);


    default Flight findEarliestFirstSeen() {
        List<Flight> flights = findAllOrderByFirstSeenAsc();
        return flights.isEmpty() ? null : flights.get(0);
    }
}
