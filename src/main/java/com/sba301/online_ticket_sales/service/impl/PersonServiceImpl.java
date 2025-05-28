package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.person.request.PersonCreationRequest;
import com.sba301.online_ticket_sales.dto.person.request.PersonUpdateRequest;
import com.sba301.online_ticket_sales.dto.person.response.PersonResponse;
import com.sba301.online_ticket_sales.entity.Person;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.enums.Occupation;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.mapper.PersonMapper;
import com.sba301.online_ticket_sales.repository.PersonRepository;
import com.sba301.online_ticket_sales.service.PersonService;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PERSON_NOT_FOUND));
        if (person.isDeleted()) {
            throw new AppException(ErrorCode.PERSON_NOT_FOUND);
        }
        personMapper.updatePersonFromRequest(request, person);
        Person updatedPerson = personRepository.save(person);
        log.info("Updated person: {}", updatedPerson.getName());
        return personMapper.toPersonResponse(updatedPerson);
    }

    @Override
    public void deletePerson(Integer id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PERSON_NOT_FOUND));
        if (person.isDeleted()) {
            throw new AppException(ErrorCode.PERSON_NOT_FOUND);
        }
        // Xóa liên kết với Movie (bảng movie_directors và movie_actors)
        person.getDirectedMovies().clear();
        person.getActedMovies().clear();
        // Đặt country thành null
        person.setCountry(null);
        person.setDeleted(true);
        personRepository.save(person);
    }

    @Override
    public Page<PersonResponse> getAllPersons(Pageable pageable, String keyword, Occupation occupation) {
        Specification<Person> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("isDeleted"), false));
            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
            }
            if (occupation != null) {
                predicates.add(cb.equal(root.get("occupation"), occupation));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Person> persons = personRepository.findAll(spec, pageable);
        return persons.map(personMapper::toPersonResponse);
    }
    @Override
    public PersonResponse getPersonDetail(Integer id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PERSON_NOT_FOUND));
        if (person.isDeleted()) {
            throw new AppException(ErrorCode.PERSON_NOT_FOUND);
        }
        return personMapper.toPersonResponse(person);
    }
}
