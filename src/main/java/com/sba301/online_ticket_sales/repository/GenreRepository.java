package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.entity.Genre;
import com.sba301.online_ticket_sales.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GenreRepository
    extends JpaRepository<Genre, Integer>, JpaSpecificationExecutor<Genre> {
    @Query("SELECT m FROM Movie m JOIN m.genres g WHERE g.id = :genreId")
    List<Movie> findMoviesByGenreId(Integer genreId);
}
