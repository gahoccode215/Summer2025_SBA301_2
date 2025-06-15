package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.cinema.request.CinemaRequest;
import com.sba301.online_ticket_sales.dto.cinema.response.CinemaDetailResponse;
import com.sba301.online_ticket_sales.dto.cinema.response.CinemaResponse;
import com.sba301.online_ticket_sales.entity.Cinema;
import com.sba301.online_ticket_sales.entity.User;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.enums.RoleEnum;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.mapper.CinemaMapper;
import com.sba301.online_ticket_sales.repository.CinemaRepository;
import com.sba301.online_ticket_sales.service.CinemaService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
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
    log.info("Upsert cinema with request: {}", request);
    var authentication =
        (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info("User: {}", authentication.getUsername());
    List<RoleEnum> roles =
        authentication.getAuthorities().stream()
            .map(authority -> RoleEnum.valueOf(authority.getAuthority()))
            .toList();
    if (request.getRequestType().isCreate() && !roles.contains(RoleEnum.ADMIN)) {
      throw new AppException(ErrorCode.CINEMA_UPSERT_PERMISSION_DENIED);
    }
    if (request.getRequestType().isUpdate() && !roles.contains(RoleEnum.ADMIN)) {
      boolean isManagerOfCinema =
          authentication.getManagedCinemas().stream()
              .anyMatch(cinema -> cinema.getId().equals(request.getId()));
      if (!isManagerOfCinema) throw new AppException(ErrorCode.CINEMA_UPSERT_PERMISSION_DENIED);
    }

    Cinema cinema = cinemaMapper.toCinema(request);
    Cinema result = cinemaRepository.save(cinema);
    log.info(request.getRequestType() + " cinema: {}", request.getId());
    return result.getId();
  }

  @Override
  public List<CinemaResponse> getAllCinemasWithAuthentication() {
    log.info("Get all cinemas");
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    List<RoleEnum> roles =
        user.getAuthorities().stream()
            .map(authority -> RoleEnum.valueOf(authority.getAuthority()))
            .toList();
    List<Cinema> cinemas;

    if (roles.contains(RoleEnum.ADMIN)) {
      cinemas = cinemaRepository.findAll();
    } else {
      cinemas = user.getManagedCinemas();
    }
    if (!cinemas.isEmpty()) {
      return cinemas.stream().map(cinemaMapper::toCinemaResponse).toList();
    }
    return List.of();
  }

  @Override
  public CinemaDetailResponse getCinemaDetail(Long id) {
    log.info("Get cinema detail with id: {}", id);
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info("User: {}", user.getUsername());
    List<RoleEnum> roles =
        user.getAuthorities().stream().map(auth -> RoleEnum.valueOf(auth.getAuthority())).toList();

    if (!roles.contains(RoleEnum.ADMIN)) {
      boolean hasAccess =
          user.getManagedCinemas().stream().anyMatch(cinema -> cinema.getId().equals(id));
      if (!hasAccess) {
        throw new AppException(ErrorCode.CINEMA_UPSERT_PERMISSION_DENIED);
      }
    }

    Cinema cinema =
        cinemaRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.CINEMA_NOT_FOUND));
    log.info("Get cinema detail: {}", id);
    return cinemaMapper.toCinemaDetailResponse(cinema);
  }

  @Override
  public void deActivate(Long id, boolean active) {
    log.info("Deactivating cinema with id: {}, active: {}", id, active);
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info("User: {}", user.getUsername());
    boolean isAdmin =
        user.getAuthorities().stream()
            .anyMatch(auth -> RoleEnum.ADMIN.name().equals(auth.getAuthority()));

    if (!isAdmin) {
      throw new AppException(ErrorCode.CINEMA_UPSERT_PERMISSION_DENIED);
    }
    Cinema cinema =
        cinemaRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.CINEMA_NOT_FOUND));
    cinema.setActive(active);
    cinemaRepository.save(cinema);
    log.info("Deactivated cinema: {}, active: {}", id, active);
  }

  @Override
  public List<CinemaResponse> getAllCinemasForCustomer() {
    log.info("Get all cinemas for customer");
    List<Cinema> cinemas = cinemaRepository.findAllByIsActiveTrue();
    if (!cinemas.isEmpty()) {
      return cinemas.stream().map(cinemaMapper::toCinemaResponse).toList();
    }
    return List.of();
  }
}
