package com.sba301.online_ticket_sales.dto.movie.response;

import com.sba301.online_ticket_sales.dto.country.response.CountryResponse;
import com.sba301.online_ticket_sales.dto.genre.response.GenreResponse;
import com.sba301.online_ticket_sales.dto.person.response.PersonResponse;
import com.sba301.online_ticket_sales.enums.AgeRestriction;
import com.sba301.online_ticket_sales.enums.MovieFormat;
import com.sba301.online_ticket_sales.enums.MovieStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieResponse {

  Long id;
  String title;
  String description;
  Integer duration;

  LocalDate releaseDate;
  LocalDate premiereDate;
  LocalDate endDate;

  String thumbnailUrl;
  String trailerUrl;

  MovieStatus movieStatus;
  AgeRestriction ageRestriction;
  List<MovieFormat> availableFormats;

  Boolean isDeleted;
  Boolean isPublished;

  LocalDateTime createdAt;
  LocalDateTime updatedAt;

  CountryResponse country;
  List<GenreResponse> genres;
  List<PersonResponse> directors;
  List<PersonResponse> actors;
}
