package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
