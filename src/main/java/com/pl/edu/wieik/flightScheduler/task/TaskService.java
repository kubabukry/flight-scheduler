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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ResourceRepository resourceRepository;
    private final FlightRepository flightRepository;
    private final OperationRepository operationRepository;

    private final Scheduler scheduler;

    public TaskService(TaskRepository taskRepository, ResourceRepository resourceRepository, FlightRepository flightRepository, OperationRepository operationRepository, Scheduler scheduler) {
        this.taskRepository = taskRepository;
        this.resourceRepository = resourceRepository;
        this.flightRepository = flightRepository;
        this.operationRepository = operationRepository;
        this.scheduler = scheduler;
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
            // Create tasks for each landing
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

    public void updatePreviousTask(Task updatedTask) {
        if(updatedTask != null){
            List<Task> previousTasks = updatedTask.getPreviousTasks();
            for (int i = 0; i < previousTasks.size(); i++) {
                if (previousTasks.get(i).getId().equals(updatedTask.getId())) {
                    previousTasks.set(i, updatedTask);
                    break;
                }
            }
        } else {
            return;
        }
    }

    @Transactional
    public void scheduleTasks(){
        List<Flight> flights = flightRepository.findAll();

        while(true){
            List<Task> flightsTasks = taskRepository.findTasksByFlights(flights);

            if(!unscheduledTasksLeft(flightsTasks)){
                break;
            }

            calculatePriorities(flightsTasks);
            Task task = scheduler.getFirstTask(flightsTasks);
            Instant currentStart = getPreviousCompleted(task);

            while(getTaskCount(task.getResource(), currentStart) >= task.getResource().getAvailable()){
                currentStart = currentStart.plus(Duration.ofMinutes(1));
            }

            task.setStarted(currentStart);
            task.setCompleted(currentStart.plus(Duration.ofMinutes(task.getOperation().getDuration())));
            task.setIsScheduled(true);
        }
    }

    @Transactional
    public void scheduleLandings() {
        List<Flight> flights = flightRepository.findAll();
        Resource runway = resourceRepository.findByName("Runway");
        for (Flight flight : flights) {
            List<Task> landingTasks = taskRepository.findLandingTasksByFlight(flight);
            for (Task task : landingTasks) {
                Instant currentStart = flight.getFirstSeen();

                while (getTaskCount(runway, currentStart) >= runway.getAvailable()) {
                    currentStart = currentStart.plus(Duration.ofMinutes(1));
                }

                task.setStarted(currentStart);
                task.setCompleted(currentStart.plus(Duration.ofMinutes(task.getOperation().getDuration())));
                task.setIsScheduled(true);
                taskRepository.save(task);
            }
        }
    }


    private void calculatePriorities(List<Task> flightsTasks) {
        for (Task task : flightsTasks) {
            if (task != null && task.getPreviousTasks() != null && task.getCompleted() != null) {
                Instant deadlinePlus15Minutes = task.getDeadline().plus(15, ChronoUnit.MINUTES);
                if (task.getCompleted().isAfter(deadlinePlus15Minutes)) {
                    task.setPriority(true);
                }
            }
        }
    }


    private boolean unscheduledTasksLeft(List<Task> flightsTasks) {
        for (Task task : flightsTasks) {
            if (!task.getIsScheduled()) {
                return true;
            }
        }
        return false;
    }

    public Instant getPreviousCompleted(Task task) {
        return task.getPreviousTasks().stream()
                .filter(t -> t.getCompleted() != null)
                .max(Comparator.comparing(Task::getCompleted))
                .map(Task::getCompleted)
                .orElse(null);
    }

    public Task getPreviousCompletedTask(Task task) {
        return task.getPreviousTasks().stream()
                .filter(t -> t.getCompleted() != null)
                .max(Comparator.comparing(Task::getCompleted))
                .orElse(null);
    }


    public int getTaskCount(Resource resource, Instant currentStart) {
        return taskRepository.countTasksWithCurrentStartBetweenStartedAndCompleted(resource, currentStart);
    }
    public void deleteAllTasks() {
        taskRepository.deleteAll();
    }

    public List<Task> getRunwayTasks() {
        return taskRepository.getRunwayTasks();
    }

}
