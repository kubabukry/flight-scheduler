package com.pl.edu.wieik.flightScheduler.configuration;

import com.pl.edu.wieik.flightScheduler.flight.FlightService;
import com.pl.edu.wieik.flightScheduler.task.Scheduler;
import com.pl.edu.wieik.flightScheduler.task.TaskService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class DataLoader implements ApplicationRunner {

    private final FlightService flightService;
    private final TaskService taskService;
    private final Scheduler scheduler;


    public DataLoader(FlightService flightService, TaskService taskService, Scheduler scheduler) {
        this.flightService = flightService;
        this.taskService = taskService;
        this.scheduler = scheduler;
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        flightService.deleteAllFlights();
        taskService.deleteAllTasks();
        flightService.populateDatabaseFromCSV();
        taskService.createTasks();
//        scheduler.scheduleLandingsAll();
//        scheduler.scheduleTasksAll();
    }
}
