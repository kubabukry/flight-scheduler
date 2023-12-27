package com.pl.edu.wieik.flightScheduler.person;

import jakarta.validation.Valid;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

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
}
