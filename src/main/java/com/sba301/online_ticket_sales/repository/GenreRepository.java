package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
