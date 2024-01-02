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

    @PostMapping("/person/auth/authenticate")
    public ResponseEntity<AuthenticationResponseDto> authenticate(
            @RequestBody AuthenticationRequest authenticationRequest){
        return ResponseEntity.ok(personService.authenticate(authenticationRequest));
    }

    @GetMapping("/person/all")
    public List<PersonDto> getPersonList(){
        return PersonMapper.mapPersonListToPersonDtoList(personService.getPersonList());
    }
}
