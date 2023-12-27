package com.pl.edu.wieik.flightScheduler.task;

import com.pl.edu.wieik.flightScheduler.flight.Flight;
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
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Operation operation;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Resource resource;
    private Instant dueDate;
    private Instant deadline;
    private Integer priority;
    private Boolean isStarted;
    private Boolean isCompleted;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "task")
    @ToString.Exclude
    private List<Person> personList;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Flight flight;

    @ManyToMany
    @JoinTable(name = "task_dependencies",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "dependency_id"))
    @ToString.Exclude
    private List<Task> dependencies;
}
