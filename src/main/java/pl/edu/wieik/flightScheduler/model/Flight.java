package pl.edu.wieik.flightScheduler.model;

import pl.edu.wieik.flightScheduler.model.Task;
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

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            mappedBy = "flight")
    @ToString.Exclude
    private List<Task> taskList;

    public Task getTaskByOperation(String name) {
        return taskList.stream()
            .filter(task -> name.equals(task.getOperation().getName()))
            .findFirst()
            .orElse(null);
    }
}
