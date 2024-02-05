package pl.edu.wieik.flightScheduler.configuration;

import pl.edu.wieik.flightScheduler.service.FlightService;
import pl.edu.wieik.flightScheduler.service.TaskService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

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
    }
}
