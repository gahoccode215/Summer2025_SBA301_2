package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.person.request.PersonCreationRequest;
import com.sba301.online_ticket_sales.dto.person.request.PersonUpdateRequest;
import com.sba301.online_ticket_sales.dto.person.response.PersonResponse;
import com.sba301.online_ticket_sales.entity.Person;
import com.sba301.online_ticket_sales.mapper.PersonMapper;
import com.sba301.online_ticket_sales.repository.PersonRepository;
import com.sba301.online_ticket_sales.service.PersonService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PersonServiceImpl implements PersonService {

    PersonRepository personRepository;
    PersonMapper personMapper;

    @Override
    public PersonResponse createPerson(PersonCreationRequest request) {
        Person person = personMapper.toPerson(request);
        person.setDeleted(false);
        Person savedPerson = personRepository.save(person);
        log.info("Created person: {}", savedPerson.getName());
        return personMapper.toPersonResponse(savedPerson);
    }

    @Override
    public PersonResponse updatePerson(Integer id, PersonUpdateRequest request) {
        return null;
    }

    @Override
    public void deletePerson(Integer id) {

    }

    @Override
    public List<PersonResponse> getAllPersons(Pageable pageable) {
        return List.of();
    }

    @Override
    public PersonResponse getPersonDetail(Integer id) {
        return null;
    }
}
