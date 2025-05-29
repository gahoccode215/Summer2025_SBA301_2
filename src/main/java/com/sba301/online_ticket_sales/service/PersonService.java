package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.person.request.PersonCreationRequest;
import com.sba301.online_ticket_sales.dto.person.request.PersonUpdateRequest;
import com.sba301.online_ticket_sales.dto.person.response.PersonResponse;
import java.awt.print.Pageable;
import java.util.List;

public interface PersonService {
  PersonResponse createPerson(PersonCreationRequest request);

  PersonResponse updatePerson(Integer id, PersonUpdateRequest request);

  void deletePerson(Integer id);

  List<PersonResponse> getAllPersons(Pageable pageable);

  PersonResponse getPersonDetail(Integer id);
}
