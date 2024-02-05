package pl.edu.wieik.flightScheduler.controller;

import org.springframework.web.bind.annotation.*;
import pl.edu.wieik.flightScheduler.dto.OperationCreationDto;
import pl.edu.wieik.flightScheduler.dto.OperationDto;
import pl.edu.wieik.flightScheduler.service.OperationService;
import pl.edu.wieik.flightScheduler.dto.OperationUpdateDto;

import java.util.List;

@RestController
public class OperationController {
    private final OperationService operationService;

    public OperationController(OperationService operationService) {
        this.operationService = operationService;
    }

    @PostMapping("/operation/create")
    public void createOperation(@RequestBody OperationCreationDto operationCreationDto) {
        operationService.createOperation(operationCreationDto);
    }

    @PutMapping("/operation/update/{id}")
    public void updateOperation(@PathVariable Long id, @RequestBody OperationUpdateDto operationUpdateDto) {
        operationService.updateOperation(id, operationUpdateDto);
    }

    @GetMapping("/operation/all")
    public List<OperationDto> getOperationList() {
        return operationService.getOperationList();
    }

    @DeleteMapping("/operation/{id}")
    public void deleteOperation(@PathVariable Long id) {
        operationService.deleteOperation(id);
    }

}
