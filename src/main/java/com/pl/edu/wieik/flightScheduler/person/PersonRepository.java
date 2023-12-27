package com.pl.edu.wieik.flightScheduler.person;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    boolean existsByLogin(String login);
    Optional<Person> findByLogin(String login);
}
