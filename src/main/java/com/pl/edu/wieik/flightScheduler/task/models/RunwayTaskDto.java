package com.pl.edu.wieik.flightScheduler.task.models;

import lombok.Data;

import java.time.Instant;

@Data
public class RunwayTaskDto {
    private Long id;
    private String operation;
    private String resource;
    private String flightNumber;
    private Instant deadline;
    private Boolean priority;
    private Instant started;
    private Instant completed;
}
