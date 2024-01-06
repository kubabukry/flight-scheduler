package com.pl.edu.wieik.flightScheduler.task;

import com.pl.edu.wieik.flightScheduler.flight.Flight;
import com.pl.edu.wieik.flightScheduler.flight.FlightRepository;
import com.pl.edu.wieik.flightScheduler.operation.Operation;
import com.pl.edu.wieik.flightScheduler.operation.OperationRepository;
import com.pl.edu.wieik.flightScheduler.resource.ResourceRepository;
import com.pl.edu.wieik.flightScheduler.task.models.RunwayTaskDto;
import com.pl.edu.wieik.flightScheduler.task.models.TaskMapper;
import org.springframework.stereotype.Service;
import com.pl.edu.wieik.flightScheduler.resource.Resource;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ResourceRepository resourceRepository;
    private final FlightRepository flightRepository;
    private final OperationRepository operationRepository;

    public TaskService(TaskRepository taskRepository, ResourceRepository resourceRepository, FlightRepository flightRepository, OperationRepository operationRepository) {
        this.taskRepository = taskRepository;
        this.resourceRepository = resourceRepository;
        this.flightRepository = flightRepository;
        this.operationRepository = operationRepository;
    }

    public void createTasks() {
        Resource runway = resourceRepository.findByName("Runway");
        Resource pilotCar = resourceRepository.findByName("Pilot Car");
        Resource parking = resourceRepository.findByName("Parking");
        Resource gateway = resourceRepository.findByName("Gateway");

        Operation landing = operationRepository.findByName("Landing");
        Operation taxiIn = operationRepository.findByName("Taxi-In");
        Operation deboarding = operationRepository.findByName("Deboarding");
        Operation unloading = operationRepository.findByName("Unloading");
        Operation fueling = operationRepository.findByName("Fueling");
        Operation catering = operationRepository.findByName("Catering");
        Operation cleaning = operationRepository.findByName("Cleaning");
        Operation loading = operationRepository.findByName("Loading");
        Operation boarding = operationRepository.findByName("Boarding");
        Operation taxiOut = operationRepository.findByName("Taxi-Out");
        Operation takeOff = operationRepository.findByName("Take-Off");

        //dependent tasks dueDate times combined
        Integer landingDueDep = landing.getDuration();
        Integer taxiInDueDep = landingDueDep + taxiIn.getDuration();
        Integer deboardingDueDep = taxiInDueDep + deboarding.getDuration();
        Integer unloadingDueDep = taxiInDueDep + unloading.getDuration();
        Integer fuelingDueDep = taxiInDueDep + fueling.getDuration();
        Integer cateringDueDep = taxiInDueDep + catering.getDuration();
        Integer cleaningDueDep = taxiInDueDep + cleaning.getDuration();
        Integer loadingDueDep = unloadingDueDep + loading.getDuration();
        Integer boardingDueDep = deboardingDueDep + boarding.getDuration();
        Integer taxiOutDueDep = boardingDueDep + taxiOut.getDuration();
        Integer takeOffDueDep = taxiOutDueDep + takeOff.getDuration();

        //dependent tasks deadline times combined
        //Integer takeOffDeadlineDep = flight.plannedDeparture;
        Integer taxiOutDeadlineDep = takeOff.getDuration();
        Integer boardingDeadlineDep = taxiOut.getDuration() + takeOff.getDuration();
        Integer loadingDeadlineDep = taxiOut.getDuration() + takeOff.getDuration();
        Integer cleaningDeadlineDep = taxiOut.getDuration() + takeOff.getDuration();
        Integer cateringDeadlineDep = taxiOut.getDuration() + takeOff.getDuration();
        Integer fuelingDeadlineDep = taxiOut.getDuration() + takeOff.getDuration();
        Integer unloadingDeadlineDep = loading.getDuration() + taxiOut.getDuration() + takeOff.getDuration();
        Integer deboardingDeadlineDep = boarding.getDuration() + taxiOut.getDuration() + takeOff.getDuration();
        Integer taxiInDeadlineDep = deboarding.getDuration() + boarding.getDuration() + taxiOut.getDuration() + takeOff.getDuration();
        //Integer landingDeadlineDep = flight.plannedArrival;


        List<Flight> flights = flightRepository.findAll();
        for (Flight flight : flights) {
            List<Task> tasks = new ArrayList<>();
            // Create tasks for each operation
            tasks.add(createTask(landing, runway,
                    flight.getFirstSeen().plus(Duration.ofMinutes(landingDueDep)),
                    flight.getPlannedArrival(), 0));

            tasks.add(createTask(taxiIn, pilotCar,
                    flight.getFirstSeen().plus(Duration.ofMinutes(taxiInDueDep)),
                    flight.getPlannedDeparture().minus(Duration.ofMinutes(taxiInDeadlineDep)), 0));

            tasks.add(createTask(deboarding, parking,
                    flight.getFirstSeen().plus(Duration.ofMinutes(deboardingDueDep)),
                    flight.getPlannedDeparture().minus(Duration.ofMinutes(deboardingDeadlineDep)), 0));

            tasks.add(createTask(unloading, parking,
                    flight.getFirstSeen().plus(Duration.ofMinutes(unloadingDueDep)),
                    flight.getPlannedDeparture().minus(Duration.ofMinutes(unloadingDeadlineDep)), 0));

            tasks.add(createTask(fueling, parking,
                    flight.getFirstSeen().plus(Duration.ofMinutes(fuelingDueDep)),
                    flight.getPlannedDeparture().minus(Duration.ofMinutes(fuelingDeadlineDep)), 0));

            tasks.add(createTask(catering, parking,
                    flight.getFirstSeen().plus(Duration.ofMinutes(cateringDueDep)),
                    flight.getPlannedDeparture().minus(Duration.ofMinutes(cateringDeadlineDep)), 0));

            tasks.add(createTask(cleaning, parking,
                    flight.getFirstSeen().plus(Duration.ofMinutes(cleaningDueDep)),
                    flight.getPlannedDeparture().minus(Duration.ofMinutes(cleaningDeadlineDep)), 0));

            tasks.add(createTask(loading, gateway,
                    flight.getFirstSeen().plus(Duration.ofMinutes(loadingDueDep)),
                    flight.getPlannedDeparture().minus(Duration.ofMinutes(loadingDeadlineDep)), 0));

            tasks.add(createTask(boarding, gateway,
                    flight.getFirstSeen().plus(Duration.ofMinutes(boardingDueDep)),
                    flight.getPlannedDeparture().minus(Duration.ofMinutes(boardingDeadlineDep)), 0));

            tasks.add(createTask(taxiOut, pilotCar,
                    flight.getFirstSeen().plus(Duration.ofMinutes(taxiOutDueDep)),
                    flight.getPlannedDeparture().minus(Duration.ofMinutes(taxiOutDeadlineDep)), 0));

            tasks.add(createTask(takeOff, runway,
                    flight.getFirstSeen().plus(Duration.ofMinutes(takeOffDueDep)),
                    flight.getPlannedDeparture(), 0));

            // Save tasks to the database
            for (Task task : tasks) {
                task.setFlight(flight);
                taskRepository.save(task);
            }
        }
    }

    private Task createTask(Operation operation, Resource resource, Instant dueDate, Instant deadline, int priority) {
        Task task = new Task();
        task.setOperation(operation);
        task.setResource(resource);
        task.setDueDate(dueDate);
        task.setDeadline(deadline);
        task.setPriority(priority);
        task.setIsStarted(false);
        task.setIsCompleted(false);
        return task;
    }

    public void deleteAllTasks() {
        taskRepository.deleteAll();
    }

    public List<RunwayTaskDto> getRunwayTasks() {
        List<Task> tasks = taskRepository.getRunwayTasks();
        return TaskMapper.mapRunwayTaskDtoList(tasks);
    }
}
