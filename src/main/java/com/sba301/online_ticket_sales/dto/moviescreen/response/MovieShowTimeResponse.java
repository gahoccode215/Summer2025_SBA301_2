package com.sba301.online_ticket_sales.dto.moviescreen.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class MovieShowTimeResponse {
  private Long movieId;
  private String movieName;
  private String moviePosterUrl;
  private int movieDuration;
  private String movieRating;
  private LocalDateTime movieReleaseDate;
  private List<ShowTimeResponse> showTimes;
}
