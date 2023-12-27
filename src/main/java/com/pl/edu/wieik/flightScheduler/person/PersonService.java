package com.pl.edu.wieik.flightScheduler.person;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PersonService implements UserDetailsService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    public PersonService(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public void createPerson(PersonCreationDto personCreationDto) {
        boolean loginExists = this.personRepository.existsByLogin(personCreationDto.getLogin());
        if(loginExists){
            throw new AlreadyExistsException(
                    "User with login: "+personCreationDto.getLogin()+" already exists"
            );
        }
        Person person = new Person();
        person.setLogin(personCreationDto.getLogin());
        person.setPassword(passwordEncoder.encode(personCreationDto.getPassword()));
        person.setRole(personCreationDto.getRole());
        person.setFirstName(personCreationDto.getFirstName());
        person.setLastName(personCreationDto.getLastName());
        person.setDateCreated(Instant.now());
        person.setDateModified(null);
        personRepository.save(person);
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return personRepository
                .findByLogin(login)
                .map(SecurityUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("Login not found: " + login));
    }

    public void updatePerson(Long id, PersonCreationDto personCreationDto) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new NoSuchContent("No user exists with id: " + id));
        person.setLogin(personCreationDto.getLogin());
        person.setPassword(passwordEncoder.encode(personCreationDto.getPassword()));
        person.setRole(personCreationDto.getRole());
        person.setFirstName(personCreationDto.getFirstName());
        person.setLastName(personCreationDto.getLastName());
        person.setDateModified(Instant.now());
        personRepository.save(person);
    }
}
