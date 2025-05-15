package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.person.request.PersonCreationRequest;

public interface PersonService {
    void createPerson(PersonCreationRequest request);
}
