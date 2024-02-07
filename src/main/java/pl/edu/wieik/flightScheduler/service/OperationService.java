package pl.edu.wieik.flightScheduler.service;

import pl.edu.wieik.flightScheduler.dto.OperationCreationDto;
import pl.edu.wieik.flightScheduler.dto.OperationDto;
import pl.edu.wieik.flightScheduler.exception.AlreadyExistsException;
import pl.edu.wieik.flightScheduler.exception.NoSuchContent;
import pl.edu.wieik.flightScheduler.mapper.OperationMapper;
import pl.edu.wieik.flightScheduler.model.Operation;
import pl.edu.wieik.flightScheduler.dto.OperationUpdateDto;
import pl.edu.wieik.flightScheduler.repository.OperationRepository;
import org.springframework.stereotype.Service;

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
