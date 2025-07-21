package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.entity.Cinema;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, Long> {
  List<Cinema> findAllByIsActiveTrue();

  @Query("SELECT COUNT(c) FROM Cinema c WHERE c.isActive = true")
  Long countByIsActiveTrue();
}
