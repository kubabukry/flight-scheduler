package com.pl.edu.wieik.flightScheduler.resource;

import com.pl.edu.wieik.flightScheduler.resource.models.ResourceCreationDto;
import com.pl.edu.wieik.flightScheduler.resource.models.ResourceDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping("resource/create")
    public void createPerson(@RequestBody ResourceCreationDto resourceCreationDto){
        resourceService.createResource(resourceCreationDto);
    }

    @PutMapping("resource/update/{id}")
    public void updateResource(@PathVariable Long id, @RequestBody ResourceCreationDto resourceCreationDto){
        resourceService.updateResource(id, resourceCreationDto);
    }

    @GetMapping("resource/all")
    public List<ResourceDto> getResourceList(){
        return resourceService.getResourceList();
    }

    @GetMapping("/resource/{id}")
    public ResourceDto getSingleResource(@PathVariable Long id){
        return resourceService.getSingleResource(id);
    }

    @DeleteMapping("/resource/{id}")
    public void deleteResource(@PathVariable Long id){
        resourceService.deleteResource(id);
    }
}
