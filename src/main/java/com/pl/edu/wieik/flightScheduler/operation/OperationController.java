package com.pl.edu.wieik.flightScheduler.operation;

import org.springframework.web.bind.annotation.*;

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
    public void updateOperation(@PathVariable Long id, @RequestBody OperationCreationDto operationCreationDto) {
        operationService.updateOperation(id, operationCreationDto);
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
