package com.pl.edu.wieik.flightScheduler.task;

import com.pl.edu.wieik.flightScheduler.flight.Flight;
import com.pl.edu.wieik.flightScheduler.operation.Operation;
import com.pl.edu.wieik.flightScheduler.person.Person;
import com.pl.edu.wieik.flightScheduler.resource.Resource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.ArrayList;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    private Operation operation;

    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    private Resource resource;
    private Instant deadline;
    private Boolean priority;
    private Instant started;
    private Instant completed;
    private Boolean isScheduled;
    private String type;


    @ManyToMany
    @JoinTable(name = "task_previous",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "previous_id"))
    private List<Task> previousTasks = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private Flight flight;

    public boolean hasPreviousTasksScheduled() {
        if (previousTasks == null) {
            return true;
        }
        for (Task task : previousTasks) {
            if (!task.getIsScheduled()) {
                return false;
            }
        }
        return true;
    }
}
