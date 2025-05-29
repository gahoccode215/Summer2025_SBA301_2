package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.country.request.CountryCreationRequest;
import com.sba301.online_ticket_sales.dto.country.request.CountryUpdateRequest;
import com.sba301.online_ticket_sales.dto.country.response.CountryResponse;
import com.sba301.online_ticket_sales.entity.Country;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.mapper.CountryMapper;
import com.sba301.online_ticket_sales.repository.CountryRepository;
import com.sba301.online_ticket_sales.service.CountryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CountryServiceImpl implements CountryService {

    CountryRepository countryRepository;
    CountryMapper countryMapper;

    @Override
    public CountryResponse createCountry(CountryCreationRequest request) {
        try {
            Country country = countryMapper.toCountry(request);
            Country savedCountry = countryRepository.save(country);
            return countryMapper.toCountryResponse(savedCountry);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.COUNTRY_ALREADY_EXISTS);
        }
    }

    @Override
    public CountryResponse updateCountry(Integer id, CountryUpdateRequest request) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COUNTRY_NOT_FOUND));
        try {
            countryMapper.updateCountryFromRequest(request, country);
            Country updatedCountry = countryRepository.save(country);
            return countryMapper.toCountryResponse(updatedCountry);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.COUNTRY_ALREADY_EXISTS);
        }
    }

    @Override
    public void deleteCountry(Integer id) {

    }

    @Override
    public CountryResponse getCountryDetail(Integer id) {
        return null;
    }
}
