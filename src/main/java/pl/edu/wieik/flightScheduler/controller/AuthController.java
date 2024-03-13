package pl.edu.wieik.flightScheduler.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.wieik.flightScheduler.dto.AuthenticationRequest;
import pl.edu.wieik.flightScheduler.dto.AuthenticationResponseDto;
import pl.edu.wieik.flightScheduler.service.PersonService;

@RestController
public class AuthController {

    private final PersonService personService;

    public AuthController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping("/login")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<AuthenticationResponseDto> authenticate(
            @RequestBody AuthenticationRequest authenticationRequest){
        return ResponseEntity.ok(personService.authenticate(authenticationRequest));
    }
}
