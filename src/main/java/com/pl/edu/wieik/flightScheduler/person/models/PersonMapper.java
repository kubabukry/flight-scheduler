package com.pl.edu.wieik.flightScheduler.person.models;

import com.pl.edu.wieik.flightScheduler.person.Person;

import java.util.List;
import java.util.stream.Collectors;

public class PersonMapper {

    public static List<PersonDto> mapPersonListToPersonDtoList(List<Person> personList){
        return personList.stream()
                .map(person -> PersonDto.builder()
                        .id(person.getId())
                        .login(person.getLogin())
                        .firstName(person.getFirstName())
                        .lastName(person.getLastName())
                        .dateCreated(person.getDateCreated())
                        .dateModified(person.getDateModified())
                        .role(person.getRole().name())
                        .build())
                .collect(Collectors.toList());
    }
}
