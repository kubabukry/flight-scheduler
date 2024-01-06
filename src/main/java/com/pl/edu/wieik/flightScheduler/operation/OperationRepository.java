package com.pl.edu.wieik.flightScheduler.operation;

import com.pl.edu.wieik.flightScheduler.resource.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
    Operation findByName(String name);
    boolean existsByName(String login);
}
