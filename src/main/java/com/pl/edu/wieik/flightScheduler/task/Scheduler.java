package com.pl.edu.wieik.flightScheduler.task;

import com.pl.edu.wieik.flightScheduler.flight.Flight;
import com.pl.edu.wieik.flightScheduler.flight.FlightRepository;
import com.pl.edu.wieik.flightScheduler.resource.Resource;
import com.pl.edu.wieik.flightScheduler.resource.ResourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    @Transactional
    public void scheduleTasks(){
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


    public Task getFirstTask(List<Task> flightsTasks) {
        // Find the task with the earliest deadline that is not scheduled
        Task earliestTask = flightsTasks.stream()
                .filter(task -> !task.getIsScheduled())
                .min(Comparator.comparing(Task::getDeadline))
                .orElse(null);

        if (earliestTask == null) {
            return null;
        }

        // Create a new list and add the earliest task
        List<Task> tasks = new ArrayList<>();
        tasks.add(earliestTask);

        // Add tasks which are not scheduled and with the same operation and deadline in range of earliestTask.deadline + earliestTask.operation.duration
        Instant deadlineRange = earliestTask.getDeadline().plus(Duration.ofMinutes(earliestTask.getOperation().getDuration()));
        flightsTasks.stream()
                .filter(task -> !task.getIsScheduled() && task.getOperation().equals(earliestTask.getOperation()) && !task.getDeadline().isAfter(deadlineRange))
                .forEach(tasks::add);

        // Filter those with priority true
        List<Task> priorityTasks = tasks.stream()
                .filter(Task::getPriority)
                .collect(Collectors.toList());

        // If there are one or more tasks with priority true, choose the one with the earliest deadline
        // If there is only one task with priority true, choose that task
        // If there are no tasks with priority true, choose the task with the earliest deadline
        Task priorityTask = priorityTasks.stream()
                .min(Comparator.comparing(Task::getDeadline))
                .orElse(earliestTask);

        return priorityTask;
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

    public int getTaskCount(Resource resource, Instant currentStart) {
        return taskRepository.countTasksWithCurrentStartBetweenStartedAndCompleted(resource, currentStart);
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
}
