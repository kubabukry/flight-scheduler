package com.pl.edu.wieik.flightScheduler.flight;

import com.pl.edu.wieik.flightScheduler.task.Task;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private Instant plannedArrival;
    private Instant plannedDeparture;
    private Instant firstSeen;
    private String destination;
    private String flightNumber;
    private Boolean isActive;
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "flight")
    @ToString.Exclude
    private List<Task> taskList;
}
