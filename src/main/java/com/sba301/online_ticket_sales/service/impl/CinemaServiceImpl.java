package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.cinema.request.CinemaRequest;
import com.sba301.online_ticket_sales.dto.cinema.response.CinemaDetailResponse;
import com.sba301.online_ticket_sales.dto.cinema.response.CinemaResponse;
import com.sba301.online_ticket_sales.entity.Cinema;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.mapper.CinemaMapper;
import com.sba301.online_ticket_sales.repository.CinemaRepository;
import com.sba301.online_ticket_sales.service.CinemaService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CinemaServiceImpl implements CinemaService {
  CinemaRepository cinemaRepository;
  CinemaMapper cinemaMapper;

  @Override
  public Long upsertCinema(CinemaRequest request) {
    Cinema cinema = cinemaMapper.toCinema(request);
    Cinema result = cinemaRepository.save(cinema);
    log.info(request.getRequestType() + " cinema: {}", request.getId());
    return result.getId();
  }

  @Override
  public List<CinemaResponse> getAllCinemas() {
    List<Cinema> cinemas = cinemaRepository.findAll();
    if (!cinemas.isEmpty()) {
      return cinemas.stream().map(cinemaMapper::toCinemaResponse).toList();
    }
    return List.of();
  }

  @Override
  public CinemaDetailResponse getCinemaDetail(Long id) {
    Cinema cinema =
        cinemaRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.CINEMA_NOT_FOUND));
    log.info("Get cinema detail: {}", id);
    return cinemaMapper.toCinemaDetailResponse(cinema);
  }

  @Override
  public void deActivate(Long id, boolean active) {
    Cinema cinema =
        cinemaRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.CINEMA_NOT_FOUND));
    cinema.setActive(active);
    cinemaRepository.save(cinema);
    log.info("Deactivated cinema: {}, active: {}", id, active);
  }
}
