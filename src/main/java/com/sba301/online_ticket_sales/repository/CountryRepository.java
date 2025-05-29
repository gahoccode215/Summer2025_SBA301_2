package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CountryRepository
    extends JpaRepository<Country, Integer>, JpaSpecificationExecutor<Country> {}
