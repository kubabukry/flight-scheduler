package pl.edu.wieik.flightScheduler.controller;

import org.springframework.http.HttpStatus;
import pl.edu.wieik.flightScheduler.service.ResourceService;
import pl.edu.wieik.flightScheduler.dto.ResourceCreationDto;
import pl.edu.wieik.flightScheduler.dto.ResourceDto;
import pl.edu.wieik.flightScheduler.dto.ResourceUpdateDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resources")

public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createResource(@RequestBody ResourceCreationDto resourceCreationDto){
        resourceService.createResource(resourceCreationDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateResource(@PathVariable Long id, @RequestBody ResourceUpdateDto resourceUpdateDto){
        resourceService.updateResource(id, resourceUpdateDto);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<ResourceDto> getResourceList(){
        return resourceService.getResourceList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public ResourceDto getSingleResource(@PathVariable Long id){
        return resourceService.getSingleResource(id);
    }

    @GetMapping("/persons/{login}")
    @ResponseStatus(value = HttpStatus.OK)
    public ResourceDto getResourceByLogin(@PathVariable String login){
        return resourceService.getResourceByLogin(login);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteResource(@PathVariable Long id){
        resourceService.deleteResource(id);
    }
}
