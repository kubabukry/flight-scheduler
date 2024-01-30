package com.pl.edu.wieik.flightScheduler.configuration;

import com.pl.edu.wieik.flightScheduler.flight.FlightService;
import com.pl.edu.wieik.flightScheduler.resource.ResourceService;
import com.pl.edu.wieik.flightScheduler.task.TaskService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class DataLoader implements ApplicationRunner {

    private final FlightService flightService;
    private final TaskService taskService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    public DataLoader(FlightService flightService, TaskService taskService) {
        this.flightService = flightService;
        this.taskService = taskService;
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        flightService.deleteAllFlights();
        taskService.deleteAllTasks();
        flightService.populateDatabaseFromCSV();
        taskService.createTasks();
        taskService.scheduleLandings();
        taskService.scheduleTasks();
    }

//    @Override
//    public void run(String... args) throws Exception {
//        scheduler.scheduleAtFixedRate(() -> {
//            try {
//                taskService.taskScheduler();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }, 0, 1, TimeUnit.MINUTES);
//    }
}
