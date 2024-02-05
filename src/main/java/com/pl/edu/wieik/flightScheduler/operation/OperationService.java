package com.pl.edu.wieik.flightScheduler.operation;

import com.pl.edu.wieik.flightScheduler.flight.Flight;
import com.pl.edu.wieik.flightScheduler.flight.FlightRepository;
import com.pl.edu.wieik.flightScheduler.person.AlreadyExistsException;
import com.pl.edu.wieik.flightScheduler.person.NoSuchContent;
import com.pl.edu.wieik.flightScheduler.resource.ResourceRepository;
import com.pl.edu.wieik.flightScheduler.task.Task;
import com.pl.edu.wieik.flightScheduler.task.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class OperationService {
    private final OperationRepository operationRepository;

    private final TaskService taskService;

    public OperationService(OperationRepository operationRepository, TaskService taskService) {
        this.operationRepository = operationRepository;
        this.taskService = taskService;
    }

    public void createOperation(OperationCreationDto operationCreationDto){
        boolean operationExists = operationRepository.existsByName(operationCreationDto.getName());
        if(operationExists){
            throw new AlreadyExistsException("Operation with name "+operationCreationDto.getName()+" already exists");
        }
        Operation operation = new Operation();
        operation.setName(operationCreationDto.getName());
        operation.setDuration(operationCreationDto.getDuration());
        operationRepository.save(operation);
    }

    public void performUpdate(Long id, OperationUpdateDto operationUpdateDto){
        Operation operation = operationRepository.findById(id)
                .orElseThrow(() -> new NoSuchContent("No operation exists with id: " + id));
        operation.setDuration(operationUpdateDto.getDuration());
        operationRepository.save(operation);
    }

    public void updateOperation(Long id, OperationUpdateDto operationUpdateDto) {
        performUpdate(id, operationUpdateDto);
        taskService.calculateNewDeadlines();
    }

    public List<OperationDto> getOperationList() {
        return OperationMapper.mapOperationListToOperationDtoList(operationRepository.findAll());
    }

    public void deleteOperation(Long id) {
        if (!operationRepository.existsById(id)) {
            throw new NoSuchContent("No operation exists with id: " + id);
        }
        operationRepository.deleteById(id);
    }
}
