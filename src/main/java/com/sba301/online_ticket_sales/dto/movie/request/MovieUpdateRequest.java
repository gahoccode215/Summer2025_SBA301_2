package com.sba301.online_ticket_sales.dto.movie.request;

import com.sba301.online_ticket_sales.enums.MovieStatus;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieUpdateRequest {
  private String title;
  private String description;

  @Positive(message = "Duration must be positive")
  private Integer duration;

  private LocalDate releaseDate;
  private String trailerUrl;
  private MovieStatus movieStatus;
  private Integer countryId;
  private List<Integer> genreIds;
  private List<Integer> directorIds;
  private List<Integer> actorIds;
}
