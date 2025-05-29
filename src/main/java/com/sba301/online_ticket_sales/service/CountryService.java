package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.country.request.CountryCreationRequest;
import com.sba301.online_ticket_sales.dto.country.request.CountryUpdateRequest;
import com.sba301.online_ticket_sales.dto.country.response.CountryResponse;

public interface CountryService {
    CountryResponse createCountry(CountryCreationRequest request);
    CountryResponse updateCountry(Integer id, CountryUpdateRequest request);
    void deleteCountry(Integer id);
    CountryResponse getCountryDetail(Integer id);
}
