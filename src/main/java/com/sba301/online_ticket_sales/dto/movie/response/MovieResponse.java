package com.sba301.online_ticket_sales.dto.movie.response;

import com.sba301.online_ticket_sales.dto.country.response.CountryResponse;
import com.sba301.online_ticket_sales.dto.genre.response.GenreResponse;
import com.sba301.online_ticket_sales.dto.person.response.PersonResponse;
import com.sba301.online_ticket_sales.enums.AgeRestriction;
import com.sba301.online_ticket_sales.enums.MovieStatus;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieResponse {
  private Long id;
  private String title;
  private String description;
  private Integer duration;
  private LocalDate releaseDate;
  private AgeRestriction ageRestriction;
  private String trailerUrl;
  private String director;
  private MovieStatus movieStatus;
  private String image;
  private CountryResponse country;
  private List<GenreResponse> genres;
  private List<PersonResponse> directors;
  private List<PersonResponse> actors;
}
