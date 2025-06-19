package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.entity.Movie;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository
    extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {
  boolean existsByTitleIgnoreCase(String title);

  @Query(
      "SELECT COUNT(m) > 0 FROM Movie m WHERE LOWER(m.title) = LOWER(:title) AND m.id != :id AND m.isDeleted = false")
  boolean existsByTitleIgnoreCaseAndIdNot(@Param("title") String title, @Param("id") Long id);

  Optional<Movie> findByIdAndIsDeleted(Long id, Boolean isDeleted);
}
