package com.sba301.online_ticket_sales.mapper.impl;

import com.sba301.online_ticket_sales.dto.person.request.PersonCreationRequest;
import com.sba301.online_ticket_sales.entity.Person;
import com.sba301.online_ticket_sales.mapper.PersonMapper;
import org.springframework.stereotype.Component;

@Component
public class PersonMapperImpl implements PersonMapper {
    @Override
    public Person toPerson(PersonCreationRequest request) {
        return Person.builder()
                .name(request.getName())
                .biography(request.getBiography())
                .birthDate(request.getBirthDate())
                .description(request.getDescription())
                .height(request.getHeight())
                .occupation(request.getOccupation())
                .build();
    }
}
