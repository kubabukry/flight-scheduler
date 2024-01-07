package com.pl.edu.wieik.flightScheduler.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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


}
