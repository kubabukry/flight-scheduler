package com.pl.edu.wieik.flightScheduler.task.models;

import com.pl.edu.wieik.flightScheduler.task.Task;

import java.util.List;
import java.util.stream.Collectors;

public class TaskMapper {
    public static List<RunwayTaskDto> mapRunwayTaskDtoList(List<Task> tasks) {
        return tasks.stream()
                .map(TaskMapper::mapToRunwayTaskDto)
                .collect(Collectors.toList());
    }

    private static RunwayTaskDto mapToRunwayTaskDto(Task task) {
        RunwayTaskDto dto = new RunwayTaskDto();
        dto.setId(task.getId());
        dto.setOperation(task.getOperation().getName());
        dto.setResource(task.getResource().getName());
        dto.setFlightNumber(task.getFlight().getFlightNumber());
        dto.setDeadline(task.getDeadline());
        dto.setPriority(task.getPriority());
        dto.setStarted(task.getStarted());
        dto.setCompleted(task.getCompleted());
        return dto;
    }
}
