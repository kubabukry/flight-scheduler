package pl.edu.wieik.flightScheduler.repository;

import pl.edu.wieik.flightScheduler.model.Flight;
import pl.edu.wieik.flightScheduler.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.wieik.flightScheduler.model.Task;

import java.time.Instant;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query(value = "SELECT task.* " +
            "FROM task " +
            "JOIN resource ON task.resource_id = resource.id " +
            "WHERE resource.name = 'Runway' " +
            "ORDER BY task.due_date ASC", nativeQuery = true)
    List<Task> getRunwayTasks();

    @Query("SELECT t FROM Task t WHERE t.resource.name = :resourceName")
    List<Task> findAllByResourceName(@Param("resourceName") String resourceName);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.resource = :resource AND t.started IS NOT NULL AND t.completed IS NOT NULL AND :currentStart BETWEEN t.started AND t.completed")
    int countTasksWithCurrentStartBetweenStartedAndCompleted(Resource resource, Instant currentStart);

    @Query("SELECT t FROM Task t WHERE t.flight = :flight AND t.operation.name = 'Landing'")
    List<Task> findLandingTasksByFlight(Flight flight);

    @Query("SELECT t FROM Task t WHERE t.flight IN :flights")
    List<Task> findTasksByFlights(@Param("flights") List<Flight> flights);

    @Query(value = "SELECT t.* FROM task t WHERE t.resource_id = :resourceId AND t.is_scheduled = true ORDER BY t.started ASC", nativeQuery = true)
    List<Task> findAllTasksByResourceId(@Param("resourceId") Long resourceId);

}
