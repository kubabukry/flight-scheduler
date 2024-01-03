package com.pl.edu.wieik.flightScheduler.person;
import com.pl.edu.wieik.flightScheduler.person.models.AuthenticationRequest;
import com.pl.edu.wieik.flightScheduler.person.models.AuthenticationResponseDto;
import com.pl.edu.wieik.flightScheduler.person.models.PersonCreationDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class PersonService implements UserDetailsService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public PersonService(PersonRepository personRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthenticationResponseDto authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getLogin(), authenticationRequest.getPassword())
        );
        Person person = personRepository.findByLogin(authenticationRequest.getLogin())
                .orElseThrow(() -> new NoSuchContent(
                        "Invalid login or password"));
        var jwtToken = jwtService.generateToken(person);

        return AuthenticationResponseDto.builder()
                .token(jwtToken)
                .build();
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
        person.setDateModified(Instant.now());
        personRepository.save(person);
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return personRepository
                .findByLogin(login)
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

    public List<Person> getPersonList() {
        return personRepository.findAllPerson();
    }

    public Person getSinglePerson(Long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new NoSuchContent("No user exists with id: " + id));
    }

    public Person getSinglePersonByLogin(String login) {
        return personRepository.findByLogin(login)
                .orElseThrow(() -> new NoSuchContent("No user with login: " + login));
    }

    public void deletePerson(Long id) {
        if(personRepository.existsById(id)){
            personRepository.deleteById(id);
        }
    }
}
