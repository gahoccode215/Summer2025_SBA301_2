package com.sba301.online_ticket_sales.mapper;

import com.sba301.online_ticket_sales.dto.cinema.request.CinemaRequest;
import com.sba301.online_ticket_sales.dto.cinema.response.CinemaDetailResponse;
import com.sba301.online_ticket_sales.dto.cinema.response.CinemaResponse;
import com.sba301.online_ticket_sales.entity.Cinema;

public interface CinemaMapper {
  Cinema toCinema(CinemaRequest request);

  CinemaResponse toCinemaResponse(Cinema cinema);

  CinemaDetailResponse toCinemaDetailResponse(Cinema cinema);
}
