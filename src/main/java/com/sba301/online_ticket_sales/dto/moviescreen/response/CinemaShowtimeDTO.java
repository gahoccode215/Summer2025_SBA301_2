package com.sba301.online_ticket_sales.dto.moviescreen.response;

import java.time.LocalDateTime;

public interface CinemaShowtimeDTO {
  Long getCinemaId();

  String getCinemaName();

  String getCinemaAddress();

  Long getShowTimeId();

  Long getRoomId();

  LocalDateTime getShowTime();

  String getRoomType();
}
