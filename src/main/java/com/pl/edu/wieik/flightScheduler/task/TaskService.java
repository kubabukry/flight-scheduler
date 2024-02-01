package com.pl.edu.wieik.flightScheduler.task;

import com.pl.edu.wieik.flightScheduler.flight.Flight;
import com.pl.edu.wieik.flightScheduler.flight.FlightRepository;
import com.pl.edu.wieik.flightScheduler.operation.Operation;
import com.pl.edu.wieik.flightScheduler.operation.OperationRepository;
import com.pl.edu.wieik.flightScheduler.resource.ResourceRepository;
import org.springframework.stereotype.Service;
import com.pl.edu.wieik.flightScheduler.resource.Resource;
import org.springframework.transaction.annotation.Transactional;

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

    public TaskService(TaskRepository taskRepository, ResourceRepository resourceRepository, FlightRepository flightRepository, OperationRepository operationRepository, Scheduler scheduler) {
        this.taskRepository = taskRepository;
        this.resourceRepository = resourceRepository;
        this.flightRepository = flightRepository;
        this.operationRepository = operationRepository;
    }


    @Transactional
    public void createTasks() {
        Resource runway = resourceRepository.findByName("Runway");
        Resource pilotCar = resourceRepository.findByName("Pilot Car");
        Resource passengerBridge = resourceRepository.findByName("Passenger Bridge");
        Resource baggageCart = resourceRepository.findByName("Baggage Cart");
        Resource fuelCar = resourceRepository.findByName("Fuel Car");
        Resource cabinCrew = resourceRepository.findByName("Cabin Crew");

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

        //deadlines calculated based on critical path
        Duration landingDueDep = Duration.ofMinutes(landing.getDuration());
        Duration taxiInDueDep = landingDueDep.plus(Duration.ofMinutes(taxiIn.getDuration()));
        Duration deboardingDueDep = taxiInDueDep.plus(Duration.ofMinutes(deboarding.getDuration()));
        Duration unloadingDueDep = taxiInDueDep.plus(Duration.ofMinutes(unloading.getDuration()));

        Duration taxiOutDueDep = Duration.ofMinutes(taxiOut.getDuration());
        Duration boardingDueDep = taxiOutDueDep.plus(Duration.ofMinutes(boarding.getDuration()));
        Duration loadingDueDep = taxiOutDueDep.plus(Duration.ofMinutes(loading.getDuration()));
        Duration cleaningDueDep = boardingDueDep.plus(Duration.ofMinutes(cleaning.getDuration()));
        Duration cateringDueDep = boardingDueDep.plus(Duration.ofMinutes(catering.getDuration()));
        Duration fuelingDueDep = boardingDueDep.plus(Duration.ofMinutes(fueling.getDuration()));

        List<Flight> flights = flightRepository.findAll();
        for (Flight flight : flights) {
            List<Task> tasks = new ArrayList<>();

            tasks.add(createTask(landing, runway, flight.getPlannedArrival(),false,false,"arrival",flight, new ArrayList<>()));
            tasks.add(createTask(taxiIn, pilotCar, flight.getPlannedArrival().plus(taxiInDueDep), false, false, "arrival", flight, List.of(tasks.get(0))));
            tasks.add(createTask(deboarding, passengerBridge, flight.getPlannedArrival().plus(deboardingDueDep), false, false, "arrival", flight, List.of(tasks.get(1))));
            tasks.add(createTask(unloading, baggageCart, flight.getPlannedArrival().plus(unloadingDueDep), false, false, "arrival", flight, List.of(tasks.get(1))));

            tasks.add(createTask(fueling, fuelCar, flight.getPlannedDeparture().minus(fuelingDueDep),false,false, "arrival", flight, List.of(tasks.get(2))));
            tasks.add(createTask(catering, cabinCrew, flight.getPlannedDeparture().minus(cateringDueDep), false, false, "arrival", flight, List.of(tasks.get(2))));
            tasks.add(createTask(cleaning, cabinCrew, flight.getPlannedDeparture().minus(cleaningDueDep), false, false, "arrival", flight, List.of(tasks.get(2))));

            tasks.add(createTask(loading, baggageCart, flight.getPlannedDeparture().minus(loadingDueDep), false, false, "departure", flight, List.of(tasks.get(3))));
            tasks.add(createTask(boarding, passengerBridge, flight.getPlannedDeparture().minus(boardingDueDep), false, false, "departure", flight, List.of(tasks.get(4), tasks.get(5), tasks.get(6))));
            tasks.add(createTask(taxiOut, pilotCar, flight.getPlannedDeparture().minus(taxiOutDueDep), false, false, "departure", flight, List.of(tasks.get(7), tasks.get(8))));
            tasks.add(createTask(takeOff, runway, flight.getPlannedDeparture(), false, false, "departure", flight, List.of(tasks.get(9))));

            // Save tasks to the database with flight assigned
            taskRepository.saveAll(tasks);
        }
    }

    private Task createTask(Operation operation, Resource resource, Instant deadline, Boolean priority,
                            Boolean isScheduled, String type, Flight flight, List<Task> previousTasks) {
        Task task = new Task();
        task.setOperation(operation);
        task.setResource(resource);
        task.setDeadline(deadline);
        task.setPriority(priority);
        task.setIsScheduled(isScheduled);
        task.setType(type);
        task.setFlight(flight);
        task.setPreviousTasks(previousTasks);
        return task;
    }

    @Transactional
    public void calculateNewDeadlines(){
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

        Duration landingDueDep = Duration.ofMinutes(landing.getDuration());
        Duration taxiInDueDep = landingDueDep.plus(Duration.ofMinutes(taxiIn.getDuration()));
        Duration deboardingDueDep = taxiInDueDep.plus(Duration.ofMinutes(deboarding.getDuration()));
        Duration unloadingDueDep = taxiInDueDep.plus(Duration.ofMinutes(unloading.getDuration()));

        Duration taxiOutDueDep = Duration.ofMinutes(taxiOut.getDuration());
        Duration boardingDueDep = taxiOutDueDep.plus(Duration.ofMinutes(boarding.getDuration()));
        Duration loadingDueDep = taxiOutDueDep.plus(Duration.ofMinutes(loading.getDuration()));
        Duration cleaningDueDep = boardingDueDep.plus(Duration.ofMinutes(cleaning.getDuration()));
        Duration cateringDueDep = boardingDueDep.plus(Duration.ofMinutes(catering.getDuration()));
        Duration fuelingDueDep = boardingDueDep.plus(Duration.ofMinutes(fueling.getDuration()));

        List<Flight> flights = flightRepository.findAllFlightsPastNow(Instant.now());

        for (Flight flight : flights) {
            Task taxiInTask = flight.getTaskList().get(1);
            taxiInTask.setDeadline(flight.getPlannedArrival().plus(taxiInDueDep));

            Task deboardingTask = flight.getTaskList().get(2);
            deboardingTask.setDeadline(flight.getPlannedArrival().plus(deboardingDueDep));

            Task unloadingTask = flight.getTaskList().get(3);
            unloadingTask.setDeadline(flight.getPlannedArrival().plus(unloadingDueDep));

            Task fuelingTask = flight.getTaskList().get(4);
            fuelingTask.setDeadline(flight.getPlannedArrival().plus(fuelingDueDep));

            Task cateringTask = flight.getTaskList().get(5);
            cateringTask.setDeadline(flight.getPlannedArrival().plus(cateringDueDep));

            Task cleaningTask = flight.getTaskList().get(6);
            cleaningTask.setDeadline(flight.getPlannedArrival().plus(cleaningDueDep));

            Task loadingTask = flight.getTaskList().get(7);
            loadingTask.setDeadline(flight.getPlannedArrival().plus(loadingDueDep));

            Task boardingTask = flight.getTaskList().get(8);
            boardingTask.setDeadline(flight.getPlannedArrival().plus(boardingDueDep));

            Task taxiOutTask = flight.getTaskList().get(9);
            taxiOutTask.setDeadline(flight.getPlannedArrival().plus(taxiOutDueDep));
        }
    }

    public void deleteAllTasks() {
        taskRepository.deleteAll();
    }
    public List<Task> getRunwayTasks() {
        return taskRepository.getRunwayTasks();
    }

    public List<Task> getTasksByResource(Long id) {
        return taskRepository.findAllTasksByResourceId(id);
    }
}
