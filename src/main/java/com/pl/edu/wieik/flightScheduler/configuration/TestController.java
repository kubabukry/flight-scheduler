package com.pl.edu.wieik.flightScheduler.configuration;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/home")
    public String home(){
        return "Hello world!";
    }

    @Secured("STAFF")
    @GetMapping("/user")
    public String user(){
        return "Hello user!";
    }

    @Secured("ADMIN")
    @GetMapping("/admin")
    public String admin(){
        return "Hello admin!";
    }
}
