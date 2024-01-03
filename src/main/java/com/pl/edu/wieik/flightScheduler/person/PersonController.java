package com.pl.edu.wieik.flightScheduler.person;

import com.pl.edu.wieik.flightScheduler.person.models.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @Secured("ADMIN")
    @PostMapping("person/create")
    public void createPerson(@Valid @RequestBody PersonCreationDto personCreationDto){
        personService.createPerson(personCreationDto);
    }

    @PutMapping("person/update/{id}")
    public void updatePerson(@PathVariable Long id,
                             @Valid @RequestBody PersonCreationDto personCreationDto){
        personService.updatePerson(id, personCreationDto);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthenticationResponseDto> authenticate(
            @RequestBody AuthenticationRequest authenticationRequest){
        return ResponseEntity.ok(personService.authenticate(authenticationRequest));
    }

    @GetMapping("/person/all")
    public List<PersonDto> getPersonList(){
        return PersonMapper.mapPersonListToPersonDtoList(personService.getPersonList());
    }

    @GetMapping("/person/{id}")
    public PersonDto getSinglePerson(@PathVariable Long id){
        return PersonMapper.mapPersonToPersonDto(personService.getSinglePerson(id));
    }

    @Secured("ADMIN")
    @GetMapping("/person/login/{login}")
    public PersonDto getSinglePersonByLogin(@PathVariable String login){
        return PersonMapper.mapPersonToPersonDto(personService.getSinglePersonByLogin(login));
    }

    @DeleteMapping("/person/delete/{id}")
    public void deletePerson(@PathVariable Long id){
        personService.deletePerson(id);
    }
}
