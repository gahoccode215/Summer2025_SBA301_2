package com.sba301.online_ticket_sales.dto.moviescreen.response;

import java.time.LocalDateTime;

public interface MovieShowtimeDTO {
  Long getShowTimeId();

  Long getRoomId();

  String getRoomType();

  LocalDateTime getShowTime();

  Long getMovieId();

  String getMovieName();

  String getMoviePosterUrl();

  Integer getMovieDuration();

  String getMovieRating();

  LocalDateTime getMovieReleaseDate();
}
