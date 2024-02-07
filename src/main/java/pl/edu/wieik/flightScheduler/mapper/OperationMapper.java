package pl.edu.wieik.flightScheduler.mapper;

import pl.edu.wieik.flightScheduler.dto.OperationDto;
import pl.edu.wieik.flightScheduler.model.Operation;

import java.util.List;
import java.util.stream.Collectors;

public class OperationMapper {
    public static List<OperationDto> mapOperationListToOperationDtoList(List<Operation> operationList){
        return operationList.stream()
                .map(operation -> OperationDto.builder()
                        .id(operation.getId())
                        .name(operation.getName())
                        .duration(operation.getDuration())
                        .build())
                .collect(Collectors.toList());
    }

    public static OperationDto mapOperationToOperationDto(Operation operation){
        return OperationDto.builder()
                .id(operation.getId())
                .name(operation.getName())
                .duration(operation.getDuration())
                .build();
    }
}
