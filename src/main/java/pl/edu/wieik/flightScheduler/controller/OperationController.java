/*
    Status codes for methods standard
    https://www.rfc-editor.org/rfc/rfc9110.html#section-9.3
 */
package pl.edu.wieik.flightScheduler.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.edu.wieik.flightScheduler.dto.OperationCreationDto;
import pl.edu.wieik.flightScheduler.dto.OperationDto;
import pl.edu.wieik.flightScheduler.service.OperationService;
import pl.edu.wieik.flightScheduler.dto.OperationUpdateDto;

import java.util.List;

@RestController
@RequestMapping("/operations")
public class OperationController {
    private final OperationService operationService;

    public OperationController(OperationService operationService) {
        this.operationService = operationService;
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createOperation(@RequestBody OperationCreationDto operationCreationDto) {
        operationService.createOperation(operationCreationDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateOperation(@PathVariable Long id, @RequestBody OperationUpdateDto operationUpdateDto) {
        operationService.updateOperation(id, operationUpdateDto);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<OperationDto> getOperationList() {
        return operationService.getOperationList();
    }

    @DeleteMapping("{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteOperation(@PathVariable Long id) {
        operationService.deleteOperation(id);
    }
}
