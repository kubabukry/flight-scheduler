package pl.edu.wieik.flightScheduler.service;

import pl.edu.wieik.flightScheduler.model.Flight;
import pl.edu.wieik.flightScheduler.repository.FlightRepository;
import pl.edu.wieik.flightScheduler.repository.TaskRepository;
import pl.edu.wieik.flightScheduler.model.Resource;
import pl.edu.wieik.flightScheduler.repository.ResourceRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.wieik.flightScheduler.model.Task;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Scheduler {
    private final FlightRepository flightRepository;
    private final TaskRepository taskRepository;
    private final ResourceRepository resourceRepository;

    public Scheduler(FlightRepository flightRepository, TaskRepository taskRepository, ResourceRepository resourceRepository) {
        this.flightRepository = flightRepository;
        this.taskRepository = taskRepository;
        this.resourceRepository = resourceRepository;
    }


    //test function for scheduling all tasks
    @Transactional
    public void scheduleTasksAll(){
        List<Flight> flights = flightRepository.findAll();

        while(true){
            List<Task> flightsTasks = taskRepository.findTasksByFlights(flights);

            if(!unscheduledTasksLeft(flightsTasks)){
                break;
            }

            calculatePriorities(flightsTasks);
            Task task = getFirstTask(flightsTasks);
            Instant currentStart = getPreviousCompleted(task);


            while(getTaskCount(task.getResource(), currentStart) >= task.getResource().getAvailable()
                    || (task.getType().equals("departure") && currentStart.isBefore(task.getDeadline().minus(Duration.ofMinutes(5))))){
                currentStart = currentStart.plus(Duration.ofMinutes(1));
            }

            task.setStarted(currentStart);
            task.setCompleted(currentStart.plus(Duration.ofMinutes(task.getOperation().getDuration())));
            task.setIsScheduled(true);
        }
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void scheduleTasks(){
        List<Flight> flights = flightRepository.findAllByFirstSeenBefore(Instant.now());

        while(true){
            List<Task> flightsTasks = taskRepository.findTasksByFlights(flights);
            if(!unscheduledTasksLeft(flightsTasks)){
                break;
            }

            calculatePriorities(flightsTasks);
            Task task = getFirstTask(flightsTasks);
            Instant currentStart = getPreviousCompleted(task);


            while(getTaskCount(task.getResource(), currentStart) >= task.getResource().getAvailable()
                    || (task.getType().equals("departure") && currentStart.isBefore(task.getDeadline().minus(Duration.ofMinutes(5))))){
                currentStart = currentStart.plus(Duration.ofMinutes(1));
            }

            task.setStarted(currentStart);
            task.setCompleted(currentStart.plus(Duration.ofMinutes(task.getOperation().getDuration())));
            task.setIsScheduled(true);
        }
    }

    @Scheduled(fixedRate = 50000)
    @Transactional
    public void scheduleLandings() {
        List<Flight> flights = flightRepository.findAllByFirstSeenBefore(Instant.now());
        Resource runway = resourceRepository.findByName("Runway");
        for (Flight flight : flights) {
            List<Task> landingTasks = taskRepository.findLandingTasksByFlight(flight);
            if(!unscheduledTasksLeft(landingTasks)){
                continue;
            }
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

    public Task getFirstTask(List<Task> flightsTasks) {
        // find the task with the earliest deadline that is not scheduled (and have previous tasks scheduled)
        Task earliestTask = flightsTasks.stream()
                .filter(task -> !task.getIsScheduled() && task.hasPreviousTasksScheduled())
                .min(Comparator.comparing(Task::getDeadline))
                .orElse(null);

        if (earliestTask == null) {
            return null;
        }

        // create a new list and add the earliest task
        List<Task> tasks = new ArrayList<>();
        tasks.add(earliestTask);

        // add tasks which are not scheduled and have previous tasks scheduled with the same operation and deadline in range of earliestTask.deadline + earliestTask.operation.duration
        Instant deadlineRange = earliestTask.getDeadline().plus(Duration.ofMinutes(earliestTask.getOperation().getDuration()));
        flightsTasks.stream()
                .filter(task -> !task.getIsScheduled()
                        && task.hasPreviousTasksScheduled()
                        && task.getOperation().equals(earliestTask.getOperation())
                        && !task.getDeadline().isAfter(deadlineRange))
                .forEach(tasks::add);

        // filter those with priority true
        List<Task> priorityTasks = tasks.stream()
                .filter(Task::getPriority)
                .collect(Collectors.toList());

        // If there are one or more tasks with priority true, choose the one with the earliest deadline
        // If there are no tasks with priority true, choose the task with the earliest deadline
        Task priorityTask = priorityTasks.stream()
                .min(Comparator.comparing(Task::getDeadline))
                .orElse(earliestTask);

        return priorityTask;
    }

    //checks if unscheduled tasks are left (for breaking while loop)
    private boolean unscheduledTasksLeft(List<Task> flightsTasks) {
        for (Task task : flightsTasks) {
            if (!task.getIsScheduled()) {
                return true;
            }
        }
        return false;
    }

    //get the latest completed time from previous tasks
    public Instant getPreviousCompleted(Task task) {
        return task.getPreviousTasks().stream()
                .filter(t -> t.getCompleted() != null)
                .max(Comparator.comparing(Task::getCompleted))
                .map(Task::getCompleted)
                .orElse(null);
    }

    //count tasks with current start between started and completed for specific resource (to check if this resource will be available then)
    public int getTaskCount(Resource resource, Instant currentStart) {
        return taskRepository.countTasksWithCurrentStartBetweenStartedAndCompleted(resource, currentStart);
    }

    //test function for scheduling all landings
    @Transactional
    public void scheduleLandingsAll() {
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
            if (task != null && task.getPreviousTasks() != null) {
                for (Task previousTask : task.getPreviousTasks()) {
                    if (previousTask != null && previousTask.getCompleted() != null) {
                        Instant deadlinePlus15Minutes = previousTask.getDeadline().plus(15, ChronoUnit.MINUTES);
                        if (previousTask.getCompleted().isAfter(deadlinePlus15Minutes)) {
                            task.setPriority(true);
                            break;
                        }
                    }
                }
            }
        }
    }
    //gets previous completed tasks, if they are completed 15 minutes past deadline priority set to true
//    private void calculatePriorities(List<Task> flightsTasks) {
//        for (Task task : flightsTasks) {
//            if (task != null && task.getPreviousTasks() != null && task.getCompleted() != null) {
//                Instant deadlinePlus15Minutes = task.getDeadline().plus(15, ChronoUnit.MINUTES);
//                if (task.getCompleted().isAfter(deadlinePlus15Minutes)) {
//                    task.setPriority(true);
//                }
//            }
//        }
//    }
}
