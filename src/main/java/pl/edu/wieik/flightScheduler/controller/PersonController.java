package pl.edu.wieik.flightScheduler.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pl.edu.wieik.flightScheduler.dto.AuthenticationRequest;
import pl.edu.wieik.flightScheduler.dto.AuthenticationResponseDto;
import pl.edu.wieik.flightScheduler.dto.PersonCreationDto;
import pl.edu.wieik.flightScheduler.dto.PersonDto;
import pl.edu.wieik.flightScheduler.mapper.PersonMapper;
import pl.edu.wieik.flightScheduler.service.PersonService;

import java.util.List;

@RestController
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @Secured("ADMIN")
    @PostMapping("/persons")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createPerson(@Valid @RequestBody PersonCreationDto personCreationDto){
        personService.createPerson(personCreationDto);
    }

    @Secured("ADMIN")
    @PutMapping("/persons/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updatePerson(@PathVariable Long id,
                             @Valid @RequestBody PersonCreationDto personCreationDto){
        personService.updatePerson(id, personCreationDto);
    }

    @PostMapping("/login")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<AuthenticationResponseDto> authenticate(
            @RequestBody AuthenticationRequest authenticationRequest){
        return ResponseEntity.ok(personService.authenticate(authenticationRequest));
    }

    @Secured("ADMIN")
    @GetMapping("/persons")
    @ResponseStatus(value = HttpStatus.OK)
    public List<PersonDto> getPersonList(){
        return PersonMapper.mapPersonListToPersonDtoList(personService.getPersonList());
    }

    @Secured("ADMIN")
    @GetMapping("/persons/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public PersonDto getSinglePerson(@PathVariable Long id){
        return PersonMapper.mapPersonToPersonDto(personService.getSinglePerson(id));
    }

    @GetMapping("/persons/find-by-login/{login}")
    @ResponseStatus(value = HttpStatus.OK)
    public PersonDto getSinglePersonByLogin(@PathVariable String login){
        return PersonMapper.mapPersonToPersonDto(personService.getSinglePersonByLogin(login));
    }

    @DeleteMapping("/persons/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deletePerson(@PathVariable Long id){
        personService.deletePerson(id);
    }
}
