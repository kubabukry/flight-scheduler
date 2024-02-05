package pl.edu.wieik.flightScheduler.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResourceDto {
    private Long id;
    private String name;
    private Integer available;
}
