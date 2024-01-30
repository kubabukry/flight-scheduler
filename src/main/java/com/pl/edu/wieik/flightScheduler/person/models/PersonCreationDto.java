package com.pl.edu.wieik.flightScheduler.person.models;

import com.pl.edu.wieik.flightScheduler.person.Role;
import com.pl.edu.wieik.flightScheduler.person.validation.ValidLogin;
import com.pl.edu.wieik.flightScheduler.person.validation.ValidName;
import com.pl.edu.wieik.flightScheduler.person.validation.ValidPassword;
import lombok.Data;

@Data
public class PersonCreationDto {
    @ValidLogin
    private String login;
    @ValidPassword
    private String password;
    @ValidName
    private String firstName;
    @ValidName
    private String lastName;
    private Role role;
}
