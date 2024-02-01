package com.pl.edu.wieik.flightScheduler.task.models;

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
