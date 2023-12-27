package com.pl.edu.wieik.flightScheduler.flight;

import com.pl.edu.wieik.flightScheduler.task.Task;
import com.pl.edu.wieik.flightScheduler.person.Person;
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
    private String icao24;
    private Instant firstSeen;
    private Boolean isActive;
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Person person;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "flight")
    @ToString.Exclude
    private List<Task> taskList;
}
