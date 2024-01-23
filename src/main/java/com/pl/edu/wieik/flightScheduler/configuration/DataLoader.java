package com.pl.edu.wieik.flightScheduler.configuration;

import com.pl.edu.wieik.flightScheduler.flight.FlightService;
import com.pl.edu.wieik.flightScheduler.task.TaskService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Timer;
import java.util.TimerTask;

@Component
public class DataLoader implements ApplicationRunner {

    private final FlightService flightService;
    private final TaskService taskService;

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
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                taskService.scheduleTasks();
            }
        }, 0, 60 * 1000); // schedule the task to run every minute
    }
}
