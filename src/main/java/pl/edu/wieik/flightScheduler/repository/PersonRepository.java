package pl.edu.wieik.flightScheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.wieik.flightScheduler.model.Person;

import java.util.List;
import java.util.Optional;


@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    boolean existsByLogin(String login);
    @Query("SELECT p FROM Person p WHERE p.login = :login")
    Optional<Person> findByLogin(@Param("login") String login);
    @Query("SELECT p FROM Person p")
    List<Person> findAllPerson();
}
