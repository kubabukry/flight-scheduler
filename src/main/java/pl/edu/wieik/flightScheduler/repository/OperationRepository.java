package pl.edu.wieik.flightScheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.wieik.flightScheduler.model.Operation;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
    Operation findByName(String name);
    boolean existsByName(String login);
}
