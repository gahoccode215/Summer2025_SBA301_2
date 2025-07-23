package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.dashboard.*;
import com.sba301.online_ticket_sales.entity.User;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.enums.PaymentStatus;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.repository.*;
import com.sba301.online_ticket_sales.service.DashboardService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

  private final MovieRepository movieRepository;
  private final CinemaRepository cinemaRepository;
  private final MovieScreenRepository movieScreenRepository;
  private final TicketOrderRepository ticketOrderRepository;

  @Override
  public DashboardResponse getDashboard(LocalDate startDate, LocalDate endDate) {
    User currentUser = getCurrentUser();

    boolean isAdmin = hasAdminAccess();

    if (isAdmin) {
      return getAdminDashboard(startDate, endDate);
    } else {
      // Manager chỉ xem dashboard của cinema mình quản lý
      Long cinemaId = getCurrentUserCinemaId(currentUser);
      return getManagerDashboard(cinemaId, startDate, endDate);
    }
  }

  @Override
  public DashboardResponse getAdminDashboard(LocalDate startDate, LocalDate endDate) {
    log.info("Getting admin dashboard from {} to {}", startDate, endDate);

    LocalDateTime startDateTime = startDate.atStartOfDay();
    LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

    return DashboardResponse.builder()
        .overview(getAdminOverview(startDateTime, endDateTime))
        .topMovies(getTopMovies(null, startDateTime, endDateTime, 10))
        .revenueChart(getRevenueChart(null, startDate, endDate))
        .cinemaPerformances(getCinemaPerformances(startDateTime, endDateTime))
        .recentBookings(getRecentBookings(null, 20))
        .build();
  }

  @Override
  public DashboardResponse getManagerDashboard(
      Long cinemaId, LocalDate startDate, LocalDate endDate) {
    log.info("Getting manager dashboard for cinema {} from {} to {}", cinemaId, startDate, endDate);

    validateCinemaAccess(cinemaId);

    LocalDateTime startDateTime = startDate.atStartOfDay();
    LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

    return DashboardResponse.builder()
        .overview(getManagerOverview(cinemaId, startDateTime, endDateTime))
        .topMovies(getTopMovies(cinemaId, startDateTime, endDateTime, 10))
        .revenueChart(getRevenueChart(cinemaId, startDate, endDate))
        .cinemaPerformances(List.of()) // Manager không cần so sánh cinema
        .recentBookings(getRecentBookings(cinemaId, 20))
        .build();
  }

  private DashboardOverview getAdminOverview(LocalDateTime startDate, LocalDateTime endDate) {
    LocalDateTime todayStart = LocalDate.now().atStartOfDay();
    LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);

    return DashboardOverview.builder()
        .totalMovies(movieRepository.countByIsDeletedFalse())
        .totalCinemas(cinemaRepository.countByIsActiveTrue())
        .totalActiveShowtimes(movieScreenRepository.countActiveShowtimes())
        .totalBookings(
            ticketOrderRepository.countByPaymentStatusAndCreatedAtBetween(
                PaymentStatus.SUCCESS, startDate, endDate))
        .totalRevenue(
            ticketOrderRepository.sumRevenueByDateRange(PaymentStatus.SUCCESS, startDate, endDate))
        .todayRevenue(
            ticketOrderRepository.sumRevenueByDateRange(
                PaymentStatus.SUCCESS, todayStart, todayEnd))
        .todayBookings(
            ticketOrderRepository.countByPaymentStatusAndCreatedAtBetween(
                PaymentStatus.SUCCESS, todayStart, todayEnd))
        .averageRating(null) // Bỏ rating
        .build();
  }

  private DashboardOverview getManagerOverview(
      Long cinemaId, LocalDateTime startDate, LocalDateTime endDate) {
    LocalDateTime todayStart = LocalDate.now().atStartOfDay();
    LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);

    return DashboardOverview.builder()
        .totalMovies(movieScreenRepository.countUniqueMoviesByCinema(cinemaId))
        .totalCinemas(1L) // Manager chỉ quản lý 1 cinema
        .totalActiveShowtimes(movieScreenRepository.countActiveShowtimesByCinema(cinemaId))
        .totalBookings(
            ticketOrderRepository.countByCinemaAndPaymentStatusAndDateRange(
                cinemaId, PaymentStatus.SUCCESS, startDate, endDate))
        .totalRevenue(
            ticketOrderRepository.sumRevenueByCinemaAndDateRange(
                cinemaId, PaymentStatus.SUCCESS, startDate, endDate))
        .todayRevenue(
            ticketOrderRepository.sumRevenueByCinemaAndDateRange(
                cinemaId, PaymentStatus.SUCCESS, todayStart, todayEnd))
        .todayBookings(
            ticketOrderRepository.countByCinemaAndPaymentStatusAndDateRange(
                cinemaId, PaymentStatus.SUCCESS, todayStart, todayEnd))
        .averageRating(null) // Bỏ rating
        .build();
  }

  private List<TopMovie> getTopMovies(
      Long cinemaId, LocalDateTime startDate, LocalDateTime endDate, int limit) {
    List<Object[]> results;

    if (cinemaId != null) {
      results =
          ticketOrderRepository.findTopMoviesByCinemaAndDateRange(
              cinemaId, PaymentStatus.SUCCESS, startDate, endDate, PageRequest.of(0, limit));
    } else {
      results =
          ticketOrderRepository.findTopMoviesByDateRange(
              PaymentStatus.SUCCESS, startDate, endDate, PageRequest.of(0, limit));
    }

    return results.stream()
        .map(
            result ->
                TopMovie.builder()
                    .movieId((Long) result[0])
                    .movieTitle((String) result[1])
                    .thumbnailUrl((String) result[2])
                    .totalBookings((Long) result[3])
                    .totalRevenue((BigDecimal) result[4])
                    .rating(null) // Bỏ rating
                    .build())
        .collect(Collectors.toList());
  }

  private List<RevenueByDate> getRevenueChart(
          Long cinemaId, LocalDate startDate, LocalDate endDate) {
    List<Object[]> results;

    if (cinemaId != null) {
      results =
              ticketOrderRepository.getRevenueByCinemaAndDateRange(
                      cinemaId, PaymentStatus.SUCCESS, startDate, endDate);
    } else {
      results =
              ticketOrderRepository.getRevenueByDateRange(PaymentStatus.SUCCESS, startDate, endDate);
    }

    return results.stream()
            .map(
                    result -> {
                      // Convert java.sql.Date to LocalDate
                      LocalDate date = result[0] instanceof java.sql.Date
                              ? ((java.sql.Date) result[0]).toLocalDate()
                              : (LocalDate) result[0];

                      return RevenueByDate.builder()
                              .date(date)
                              .revenue((BigDecimal) result[1])
                              .bookings((Long) result[2])
                              .build();
                    })
            .collect(Collectors.toList());
  }


  private List<CinemaPerformance> getCinemaPerformances(
      LocalDateTime startDate, LocalDateTime endDate) {
    List<Object[]> results =
        ticketOrderRepository.getCinemaPerformanceByDateRange(
            PaymentStatus.SUCCESS, startDate, endDate);

    return results.stream()
        .map(
            result ->
                CinemaPerformance.builder()
                    .cinemaId((Long) result[0])
                    .cinemaName((String) result[1])
                    .totalBookings((Long) result[2])
                    .totalRevenue((BigDecimal) result[3])
                    .occupancyRate((Double) result[4])
                    .build())
        .collect(Collectors.toList());
  }

  private List<RecentBooking> getRecentBookings(Long cinemaId, int limit) {
    List<Object[]> results;

    if (cinemaId != null) {
      results =
          ticketOrderRepository.findRecentBookingsByCinema(cinemaId, PageRequest.of(0, limit));
    } else {
      results = ticketOrderRepository.findRecentBookings(PageRequest.of(0, limit));
    }

    return results.stream()
        .map(
            result ->
                RecentBooking.builder()
                    .ticketCode((String) result[0])
                    .customerName((String) result[1])
                    .movieTitle((String) result[2])
                    .cinemaName((String) result[3])
                    .bookingTime((LocalDateTime) result[4])
                    .totalAmount((BigDecimal) result[5])
                    .paymentStatus(result[6].toString())
                    .build())
        .collect(Collectors.toList());
  }

  // ... các methods còn lại giữ nguyên
  private User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    if (authentication.getPrincipal().equals("anonymousUser")) {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    return (User) authentication.getPrincipal();
  }

  private boolean hasAdminAccess() {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if (authentication == null || !authentication.isAuthenticated()) {
        return false;
      }

      if (authentication.getPrincipal().equals("anonymousUser")) {
        return false;
      }

      return authentication.getAuthorities().stream()
          .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

    } catch (Exception e) {
      log.warn("Error checking admin access: {}", e.getMessage());
      return false;
    }
  }

  private Long getCurrentUserCinemaId(User user) {
    if (user.getManagedCinemas() == null || user.getManagedCinemas().isEmpty()) {
      throw new AppException(ErrorCode.NO_PERMISSION_TO_BOOK);
    }

    return user.getManagedCinemas().get(0).getId();
  }

  private void validateCinemaAccess(Long cinemaId) {
    User currentUser = getCurrentUser();

    if (hasAdminAccess()) {
      return; // Admin có thể xem tất cả
    }

    boolean hasAccess =
        currentUser.getManagedCinemas().stream()
            .anyMatch(cinema -> cinema.getId().equals(cinemaId));

    if (!hasAccess) {
      throw new AppException(ErrorCode.NO_PERMISSION_TO_BOOK);
    }
  }
}
