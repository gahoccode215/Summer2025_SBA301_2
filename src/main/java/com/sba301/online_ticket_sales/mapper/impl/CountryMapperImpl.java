package com.sba301.online_ticket_sales.mapper.impl;

import com.sba301.online_ticket_sales.dto.country.request.CountryCreationRequest;
import com.sba301.online_ticket_sales.dto.country.request.CountryUpdateRequest;
import com.sba301.online_ticket_sales.dto.country.response.CountryResponse;
import com.sba301.online_ticket_sales.entity.Country;
import com.sba301.online_ticket_sales.mapper.CountryMapper;
import org.springframework.stereotype.Component;

@Component
public class CountryMapperImpl implements CountryMapper {

  @Override
  public Country toCountry(CountryCreationRequest request) {
    return Country.builder().name(request.getName()).build();
  }

  @Override
  public CountryResponse toCountryResponse(Country country) {
    CountryResponse response = new CountryResponse();
    response.setId(country.getId());
    response.setName(country.getName());
    return response;
  }

  @Override
  public void updateCountryFromRequest(CountryUpdateRequest request, Country country) {
    country.setName(request.getName());
  }
}
