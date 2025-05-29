package com.sba301.online_ticket_sales.mapper;

import com.sba301.online_ticket_sales.dto.country.request.CountryCreationRequest;
import com.sba301.online_ticket_sales.dto.country.response.CountryResponse;
import com.sba301.online_ticket_sales.entity.Country;

public interface CountryMapper {
    Country toCountry(CountryCreationRequest request);
    CountryResponse toCountryResponse(Country country);
}
