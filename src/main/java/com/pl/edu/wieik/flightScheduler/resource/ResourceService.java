package com.pl.edu.wieik.flightScheduler.resource;

import com.pl.edu.wieik.flightScheduler.person.NoSuchContent;
import com.pl.edu.wieik.flightScheduler.resource.models.ResourceCreationDto;
import com.pl.edu.wieik.flightScheduler.resource.models.ResourceDto;
import com.pl.edu.wieik.flightScheduler.resource.models.ResourceMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public void createResource(ResourceCreationDto resourceCreationDto) {
        Resource resource = new Resource();
        resource.setName(resourceCreationDto.getName());
        resource.setAvailable(resourceCreationDto.getAvailable());
        resourceRepository.save(resource);
    }

    public void updateResource(Long id, ResourceCreationDto resourceCreationDto) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new NoSuchContent("No resource exists with id: " + id));
        resource.setName(resourceCreationDto.getName());
        resource.setAvailable(resourceCreationDto.getAvailable());
        resourceRepository.save(resource);
    }

    public List<ResourceDto> getResourceList() {
        List<Resource> resources = resourceRepository.findAll();
        return ResourceMapper.mapResourceListToResourceDtoList(resources);
    }

    public ResourceDto getSingleResource(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new NoSuchContent("No resource exists with id: " + id));
        return ResourceMapper.mapResourceToResourceDto(resource);
    }

}
