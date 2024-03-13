package pl.edu.wieik.flightScheduler.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.edu.wieik.flightScheduler.service.TaskService;
import pl.edu.wieik.flightScheduler.dto.TaskDto;
import pl.edu.wieik.flightScheduler.mapper.TaskMapper;

import java.util.List;

@RestController
@RequestMapping("/tasks")

public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/resources/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public List<TaskDto> getTasksByResource(@PathVariable Long id){
        return TaskMapper.mapTaskListToTaskDtoList(taskService.getTasksByResource(id));
    }
}
