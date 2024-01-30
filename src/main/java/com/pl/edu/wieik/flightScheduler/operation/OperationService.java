package com.pl.edu.wieik.flightScheduler.operation;

import com.pl.edu.wieik.flightScheduler.person.AlreadyExistsException;
import com.pl.edu.wieik.flightScheduler.person.NoSuchContent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationService {
    private final OperationRepository operationRepository;

    public OperationService(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
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

    public void updateOperation(Long id, OperationCreationDto operationCreationDto) {
        Operation operation = operationRepository.findById(id)
                .orElseThrow(() -> new NoSuchContent("No operation exists with id: " + id));
        operation.setName(operationCreationDto.getName());
        operation.setDuration(operationCreationDto.getDuration());
        operationRepository.save(operation);
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
