package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.person.request.PersonCreationRequest;
import com.sba301.online_ticket_sales.dto.person.request.PersonUpdateRequest;
import com.sba301.online_ticket_sales.dto.person.response.PersonResponse;
import com.sba301.online_ticket_sales.enums.Occupation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PersonService {
    PersonResponse createPerson(PersonCreationRequest request);
    PersonResponse updatePerson(Integer id, PersonUpdateRequest request);
    void deletePerson(Integer id);
    Page<PersonResponse> getAllPersons(Pageable pageable, String keyword, Occupation occupation);
    PersonResponse getPersonDetail(Integer id);
}
