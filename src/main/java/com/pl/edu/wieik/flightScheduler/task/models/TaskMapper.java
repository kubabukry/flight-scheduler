package com.pl.edu.wieik.flightScheduler.task.models;

import com.pl.edu.wieik.flightScheduler.task.Task;

import java.util.List;
import java.util.stream.Collectors;

public class TaskMapper {

    private static TaskDto mapTaskToTaskDto(Task task){
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setDeadline(task.getDeadline());
        dto.setFlightNumber(task.getFlight().getFlightNumber());
        dto.setResourceName(task.getResource().getName());
        dto.setOperationName(task.getOperation().getName());
        dto.setStart(task.getStarted());
        dto.setFinish(task.getCompleted());
        if(task.getPriority()){
            dto.setPriority("Yes");
        } else {
            dto.setPriority("No");
        }
        return dto;
    }

    public static List<TaskDto> mapTaskListToTaskDtoList(List<Task> taskList){
        return taskList.stream()
                .map(TaskMapper::mapTaskToTaskDto)
                .collect(Collectors.toList());
    }
}
