package com.pl.edu.wieik.flightScheduler.configuration;

import com.pl.edu.wieik.flightScheduler.flight.FlightService;
import com.pl.edu.wieik.flightScheduler.task.Scheduler;
import com.pl.edu.wieik.flightScheduler.task.TaskService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

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
        scheduler.scheduleLandings();
        scheduler.scheduleTasks();
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
