package com.pl.edu.wieik.flightScheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FlightSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlightSchedulerApplication.class, args);
	}

}
