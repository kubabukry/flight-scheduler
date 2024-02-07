package pl.edu.wieik.flightScheduler.controller;

import jakarta.validation.Valid;
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
    @PostMapping("person/create")
    public void createPerson(@Valid @RequestBody PersonCreationDto personCreationDto){
        personService.createPerson(personCreationDto);
    }

    @Secured("ADMIN")
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

    @Secured("ADMIN")
    @GetMapping("/person/all")
    public List<PersonDto> getPersonList(){
        return PersonMapper.mapPersonListToPersonDtoList(personService.getPersonList());
    }

    @Secured("ADMIN")
    @GetMapping("/person/{id}")
    public PersonDto getSinglePerson(@PathVariable Long id){
        return PersonMapper.mapPersonToPersonDto(personService.getSinglePerson(id));
    }

    @GetMapping("/person/login/{login}")
    public PersonDto getSinglePersonByLogin(@PathVariable String login){
        return PersonMapper.mapPersonToPersonDto(personService.getSinglePersonByLogin(login));
    }

    @DeleteMapping("/person/delete/{id}")
    public void deletePerson(@PathVariable Long id){
        personService.deletePerson(id);
    }
}
