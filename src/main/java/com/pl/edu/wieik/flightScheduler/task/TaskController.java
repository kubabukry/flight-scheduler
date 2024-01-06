package com.pl.edu.wieik.flightScheduler.task;

import com.pl.edu.wieik.flightScheduler.task.models.RunwayTaskDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/task/runway")
    public List<RunwayTaskDto> getRunwayTasks() {
        return taskService.getRunwayTasks();
    }
}
