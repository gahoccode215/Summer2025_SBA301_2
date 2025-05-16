package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.person.request.PersonCreationRequest;
import com.sba301.online_ticket_sales.mapper.PersonMapper;
import com.sba301.online_ticket_sales.repository.PersonRepository;
import com.sba301.online_ticket_sales.service.PersonService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PersonServiceImpl implements PersonService {

    PersonRepository personRepository;
    PersonMapper personMapper;

    @Override
    public void createPerson(PersonCreationRequest request) {
        personRepository.save(personMapper.toPerson(request));
    }
}
