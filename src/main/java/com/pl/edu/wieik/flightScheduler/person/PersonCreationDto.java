package com.pl.edu.wieik.flightScheduler.person;

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
