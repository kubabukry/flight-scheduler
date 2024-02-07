package pl.edu.wieik.flightScheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.wieik.flightScheduler.model.Resource;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    Resource findByName(String name);

    @Query(value = "SELECT r.* FROM resource r JOIN person p ON r.id = p.resource_id WHERE p.login = :login", nativeQuery = true)
    Resource findByPersonLogin(@Param("login") String login);
}
