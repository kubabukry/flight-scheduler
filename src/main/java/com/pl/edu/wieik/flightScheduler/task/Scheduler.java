package com.pl.edu.wieik.flightScheduler.task;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Scheduler {

    private final TaskRepository taskRepository;

    public Scheduler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
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

    @Transactional
    public void updateTask(Task task, Instant currentStart){
        task.setStarted(currentStart);
        task.setCompleted(currentStart.plus(Duration.ofMinutes(task.getOperation().getDuration())));
        task.setIsScheduled(true);
        taskRepository.save(task);
    }
}
