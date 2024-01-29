package com.pl.edu.wieik.flightScheduler.resource;

import com.pl.edu.wieik.flightScheduler.person.Person;
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
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;
    private Integer available;
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "resource")
    @ToString.Exclude
    private List<Task> taskList;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "resource")
    @ToString.Exclude
    private List<Person> personList;
}