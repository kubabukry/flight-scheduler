package pl.edu.wieik.flightScheduler.dto;

import lombok.Data;

@Data
public class OperationCreationDto {
    private String name;
    private Integer duration;
}
