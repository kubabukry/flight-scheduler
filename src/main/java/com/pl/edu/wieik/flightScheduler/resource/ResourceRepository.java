package com.pl.edu.wieik.flightScheduler.resource;

import com.pl.edu.wieik.flightScheduler.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    Resource findByName(String name);

    @Query(value = "SELECT r.* FROM resource r JOIN person p ON r.id = p.resource_id WHERE p.login = :login", nativeQuery = true)
    Resource findByPersonLogin(@Param("login") String login);
}
