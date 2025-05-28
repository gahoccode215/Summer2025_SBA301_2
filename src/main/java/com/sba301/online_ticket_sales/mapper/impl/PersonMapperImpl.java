package com.sba301.online_ticket_sales.mapper.impl;

import com.sba301.online_ticket_sales.dto.person.request.PersonCreationRequest;
import com.sba301.online_ticket_sales.dto.person.request.PersonUpdateRequest;
import com.sba301.online_ticket_sales.dto.person.response.PersonResponse;
import com.sba301.online_ticket_sales.entity.Country;
import com.sba301.online_ticket_sales.entity.Person;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.mapper.PersonMapper;
import com.sba301.online_ticket_sales.repository.CountryRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PersonMapperImpl implements PersonMapper {
    private final CountryRepository countryRepository;

    public PersonMapperImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public Person toPerson(PersonCreationRequest request) {
        Country country = null;
        if (request.getCountryId() != null) {
            country = countryRepository.findById(request.getCountryId())
                    .orElseThrow(() -> new AppException(ErrorCode.COUNTRY_NOT_FOUND));
        }
        return Person.builder()
                .name(request.getName())
                .description(request.getDescription())
                .birthDate(request.getBirthDate())
                .height(request.getHeight())
                .occupation(request.getOccupation())
                .biography(request.getBiography())
                .country(country)
                .images(request.getImages())
                .build();
    }

    @Override
    public PersonResponse toPersonResponse(Person person) {
        PersonResponse response = new PersonResponse();
        response.setId(person.getId());
        response.setName(person.getName());
        response.setDescription(person.getDescription());
        response.setBirthDate(person.getBirthDate());
        response.setHeight(person.getHeight());
        response.setOccupation(person.getOccupation());
        response.setBiography(person.getBiography());
        response.setCountry(person.getCountry() != null ? person.getCountry().getName() : null);
        response.setImages(person.getImages());
        return response;
    }

    @Override
    public void updatePersonFromRequest(PersonUpdateRequest request, Person person) {
        Optional.ofNullable(request.getName()).filter(name -> !name.isBlank()).ifPresent(person::setName);
        Optional.ofNullable(request.getDescription()).ifPresent(person::setDescription);
        Optional.ofNullable(request.getBirthDate()).ifPresent(person::setBirthDate);
        Optional.ofNullable(request.getHeight()).ifPresent(person::setHeight);
        Optional.ofNullable(request.getOccupation()).ifPresent(person::setOccupation);
        Optional.ofNullable(request.getBiography()).ifPresent(person::setBiography);
        if (request.getCountryId() != null) {
            Country country = countryRepository.findById(request.getCountryId())
                    .orElseThrow(() -> new AppException(ErrorCode.COUNTRY_NOT_FOUND));
            person.setCountry(country);
        }
        Optional.ofNullable(request.getImages()).ifPresent(person::setImages);
    }
}
