package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.moviescreen.request.UpsertMovieScreenRequest;
import com.sba301.online_ticket_sales.dto.moviescreen.response.CinemaShowTimeResponse;
import com.sba301.online_ticket_sales.dto.moviescreen.response.MovieShowTimeResponse;
import com.sba301.online_ticket_sales.enums.MovieScreenStatus;
import java.time.LocalDateTime;
import java.util.List;

public interface MovieScheduleService {
  void createMovieSchedule(UpsertMovieScreenRequest request);

  void updateMovieSchedule(Long id, UpsertMovieScreenRequest request);

  void activateMovieSchedule(Long id, MovieScreenStatus status);

  List<CinemaShowTimeResponse> getMovieShowTimes(Long movieId, LocalDateTime date);

  List<MovieShowTimeResponse> getMovieShowTimesByCinema(Long cinemaId, LocalDateTime date);
}
