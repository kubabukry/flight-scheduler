package com.pl.edu.wieik.flightScheduler.operation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OperationDto {
    private Long id;
    private String name;
    private Integer duration;
}
