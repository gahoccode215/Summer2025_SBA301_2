package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, String> {
}
