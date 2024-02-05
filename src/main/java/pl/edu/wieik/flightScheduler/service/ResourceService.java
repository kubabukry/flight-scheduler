package pl.edu.wieik.flightScheduler.service;

import pl.edu.wieik.flightScheduler.exception.NoSuchContent;
import pl.edu.wieik.flightScheduler.model.Resource;
import pl.edu.wieik.flightScheduler.repository.ResourceRepository;
import pl.edu.wieik.flightScheduler.dto.ResourceCreationDto;
import pl.edu.wieik.flightScheduler.dto.ResourceDto;
import pl.edu.wieik.flightScheduler.mapper.ResourceMapper;
import pl.edu.wieik.flightScheduler.dto.ResourceUpdateDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void updateResource(Long id, ResourceUpdateDto resourceUpdateDto) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new NoSuchContent("No such resource exists with id: " + id));
        resource.setAvailable(resourceUpdateDto.getAvailable());
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

    public ResourceDto getResourceByLogin(String login){
        Resource resource = resourceRepository.findByPersonLogin(login);
        return ResourceMapper.mapResourceToResourceDto(resource);
    }
    public void deleteResource(Long id){
        resourceRepository.deleteById(id);
    }

}
