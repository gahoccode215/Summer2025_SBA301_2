package com.sba301.online_ticket_sales.dto.movie.response;

import com.sba301.online_ticket_sales.enums.MovieStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class MovieResponse {
    private Long id;
    private String title;
    private String description;
    private Integer duration;
    private LocalDate releaseDate;
    private String trailerUrl;
    private String director;
    private MovieStatus movieStatus;
    private String country;
    private List<String> genres;
    private List<String> directors;
    private List<String> actors;
}
