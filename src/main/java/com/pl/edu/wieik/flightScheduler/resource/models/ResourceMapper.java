package com.pl.edu.wieik.flightScheduler.resource.models;

import com.pl.edu.wieik.flightScheduler.resource.Resource;

import java.util.List;
import java.util.stream.Collectors;

public class ResourceMapper {

    public static List<ResourceDto> mapResourceListToResourceDtoList(List<Resource> resourceList){
        return resourceList.stream()
                .map(resource -> ResourceDto.builder()
                        .id(resource.getId())
                        .name(resource.getName())
                        .available(resource.getAvailable())
                        .build())
                .collect(Collectors.toList());
    }

    public static ResourceDto mapResourceToResourceDto(Resource resource){
        return ResourceDto.builder()
                .id(resource.getId())
                .name(resource.getName())
                .available(resource.getAvailable())
                .build();
    }
}
