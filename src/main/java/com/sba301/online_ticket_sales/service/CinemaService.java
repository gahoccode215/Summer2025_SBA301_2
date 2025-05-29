package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.cinema.request.CinemaRequest;
import com.sba301.online_ticket_sales.dto.cinema.response.CinemaDetailResponse;
import com.sba301.online_ticket_sales.dto.cinema.response.CinemaResponse;
import java.util.List;

public interface CinemaService {
  Long upsertCinema(CinemaRequest request);

  List<CinemaResponse> getAllCinemas();

  CinemaDetailResponse getCinemaDetail(Long id);

  void deActivate(Long id, boolean active);
}
