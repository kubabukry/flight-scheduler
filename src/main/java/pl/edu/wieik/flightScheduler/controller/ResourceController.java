package pl.edu.wieik.flightScheduler.controller;

import pl.edu.wieik.flightScheduler.service.ResourceService;
import pl.edu.wieik.flightScheduler.dto.ResourceCreationDto;
import pl.edu.wieik.flightScheduler.dto.ResourceDto;
import pl.edu.wieik.flightScheduler.dto.ResourceUpdateDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping("/resource/create")
    public void createResource(@RequestBody ResourceCreationDto resourceCreationDto){
        resourceService.createResource(resourceCreationDto);
    }

    @PutMapping("/resource/update/{id}")
    public void updateResource(@PathVariable Long id, @RequestBody ResourceUpdateDto resourceUpdateDto){
        resourceService.updateResource(id, resourceUpdateDto);
    }

    @GetMapping("/resource/all")
    public List<ResourceDto> getResourceList(){
        return resourceService.getResourceList();
    }

    @GetMapping("/resource/{id}")
    public ResourceDto getSingleResource(@PathVariable Long id){
        return resourceService.getSingleResource(id);
    }

    @GetMapping("/resource/person/{login}")
    public ResourceDto getResourceByLogin(@PathVariable String login){
        return resourceService.getResourceByLogin(login);
    }

    @DeleteMapping("/resource/{id}")
    public void deleteResource(@PathVariable Long id){
        resourceService.deleteResource(id);
    }
}
