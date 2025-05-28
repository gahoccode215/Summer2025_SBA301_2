package com.sba301.online_ticket_sales.mapper;

import com.sba301.online_ticket_sales.dto.person.request.PersonCreationRequest;
import com.sba301.online_ticket_sales.dto.person.request.PersonUpdateRequest;
import com.sba301.online_ticket_sales.dto.person.response.PersonResponse;
import com.sba301.online_ticket_sales.entity.Person;

public interface PersonMapper {
    Person toPerson(PersonCreationRequest request);
    PersonResponse toPersonResponse(Person person);
    void updatePersonFromRequest(PersonUpdateRequest request, Person person);
}
