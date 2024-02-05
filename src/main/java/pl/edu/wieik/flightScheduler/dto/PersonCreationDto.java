package pl.edu.wieik.flightScheduler.dto;

import pl.edu.wieik.flightScheduler.model.Role;
import pl.edu.wieik.flightScheduler.validation.ValidLogin;
import pl.edu.wieik.flightScheduler.validation.ValidName;
import pl.edu.wieik.flightScheduler.validation.ValidPassword;
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
