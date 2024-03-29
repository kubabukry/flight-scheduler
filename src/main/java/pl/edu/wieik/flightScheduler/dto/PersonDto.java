package pl.edu.wieik.flightScheduler.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class PersonDto {
    private Long id;
    private String login;
    private String firstName;
    private String lastName;
    private Instant dateCreated;
    private Instant dateModified;
    private String role;
}
