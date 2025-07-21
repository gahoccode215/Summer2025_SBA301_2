package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.entity.Person;
import com.sba301.online_ticket_sales.enums.Occupation;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PersonRepository
    extends JpaRepository<Person, Integer>, JpaSpecificationExecutor<Person> {
  Page<Person> findAllByIsDeletedFalse(Pageable pageable);

  List<Person> findByOccupation(Occupation occupation);
}
