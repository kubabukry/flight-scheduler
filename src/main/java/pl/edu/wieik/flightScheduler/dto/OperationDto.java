package pl.edu.wieik.flightScheduler.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OperationDto {
    private Long id;
    private String name;
    private Integer duration;
}
