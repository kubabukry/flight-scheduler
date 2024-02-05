package com.pl.edu.wieik.flightScheduler.task;

import com.pl.edu.wieik.flightScheduler.task.models.TaskDto;
import com.pl.edu.wieik.flightScheduler.task.models.TaskMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/task/resource/{id}")
    public List<TaskDto> getTasksByResource(@PathVariable Long id){
        return TaskMapper.mapTaskListToTaskDtoList(taskService.getTasksByResource(id));
    }
}
