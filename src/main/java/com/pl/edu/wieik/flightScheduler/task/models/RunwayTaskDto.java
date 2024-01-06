package com.pl.edu.wieik.flightScheduler.task.models;

import lombok.Data;

import java.time.Instant;

@Data
public class RunwayTaskDto {
    private Long id;
    private String operation;
    private String resource;
    private String flightNumber;
    private Instant dueDate;
    private Instant deadline;
    private int priority;
    private boolean isStarted;
    private boolean isCompleted;
}
