package pl.edu.wieik.flightScheduler.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class TaskDto {
    private Long id;
    private Instant deadline;
    private String flightNumber;
    private String resourceName;
    private String operationName;
    private Instant start;
    private Instant finish;
    private String priority;
}
