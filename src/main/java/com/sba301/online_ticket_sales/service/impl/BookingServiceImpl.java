package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.booking.request.BookingTicketRequest;
import com.sba301.online_ticket_sales.dto.booking.response.BookingSeatResponse;
import com.sba301.online_ticket_sales.dto.booking.response.SeatMapResponse;
import com.sba301.online_ticket_sales.dto.booking.response.TicketHistoryResponse;
import com.sba301.online_ticket_sales.dto.booking.response.TicketOrderDTO;
import com.sba301.online_ticket_sales.entity.*;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.repository.MovieRepository;
import com.sba301.online_ticket_sales.repository.MovieScreenRepository;
import com.sba301.online_ticket_sales.repository.RoomRepository;
import com.sba301.online_ticket_sales.repository.TicketOrderRepository;
import com.sba301.online_ticket_sales.service.BookingCacheService;
import com.sba301.online_ticket_sales.service.BookingService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
  private final MovieRepository movieRepository;
  private final RoomRepository roomRepository;
  private final MovieScreenRepository movieScreenRepository;
  private final BookingCacheService bookingCacheService;
  private final TicketOrderRepository ticketOrderRepository;

  @Override
  public SeatMapResponse getSeatMap(Long movieScreenId) {
    log.info("Fetching seat map for showtime ID: {}", movieScreenId);
    MovieScreen movieScreen =
        movieScreenRepository
            .findById(movieScreenId)
            .orElseThrow(() -> new AppException(ErrorCode.MOVIESCREEN_NOT_WORKING));
    List<String> availableSeatsDb = ticketOrderRepository.findSeatCodesByShowtimeId(movieScreenId);
    List<String> heldSeats = bookingCacheService.getHeldSeats(movieScreenId);

    List<String> bookedSeats = new ArrayList<>();
    bookedSeats.addAll(availableSeatsDb);
    bookedSeats.addAll(heldSeats);

    SeatMapResponse seatMapResponse =
        SeatMapResponse.builder()
            .cinemaId(movieScreen.getRoom().getCinema().getId())
            .cinemaName(movieScreen.getRoom().getCinema().getName())
            .showtimeId(movieScreen.getId())
            .showtimeTimeStart(movieScreen.getShowtime())
            .showtimeTimeEnd(
                movieScreen
                    .getShowtime()
                    .plusMinutes(movieScreen.getMovie().getDuration())
                    .plusMinutes(15))
            .movieId(movieScreen.getMovie().getId())
            .movieName(movieScreen.getMovie().getTitle())
            .moviePosterUrl(movieScreen.getMovie().getThumbnailUrl())
            .roomId(movieScreen.getRoom().getId())
            .roomType(movieScreen.getRoom().getRoomType())
            .rowNumber(movieScreen.getRoom().getRoomType().getColumns())
            .columnNumber(movieScreen.getRoom().getRoomType().getRows())
            .roomName(movieScreen.getRoom().getName())
            .ticketPrice(movieScreen.getTicketPrice())
            .totalSeatBooked(bookedSeats.size())
            .bookedSeats(bookedSeats)
            .build();

    log.info("Seat map response created: {}", seatMapResponse);
    return seatMapResponse;
  }

  @Override
  public BookingSeatResponse bookSeatsByManager(
      BookingTicketRequest bookingTicketRequest, Long cinemaId, Long customerId) {
    log.info("Booking seats by manager for request: {}", bookingTicketRequest);

    var authentication =
        (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    List<String> roleNames =
        authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    boolean isAdmin =
        roleNames.contains("MANAGER")
            || roleNames.contains("ROLE_MANAGER")
            || roleNames.contains("ADMIN")
            || roleNames.contains("ROLE_ADMIN");

    if (!isAdmin) {
      boolean hasAccess =
          authentication.getManagedCinemas().stream()
              .anyMatch(cinema -> cinema.getId().equals(cinemaId));
      if (!hasAccess) {
        throw new AppException(ErrorCode.NO_PERMISSION_TO_BOOK);
      }
    }

    return processBooking(bookingTicketRequest, customerId);
  }

  @Override
  public BookingSeatResponse bookSeats(BookingTicketRequest bookingTicketRequest) {
    log.info("Booking seats for request: {}", bookingTicketRequest);
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return processBooking(bookingTicketRequest, user.getId());
  }

  private BookingSeatResponse processBooking(BookingTicketRequest request, Long userId) {
    MovieScreen movieScreen =
        movieScreenRepository
            .findById(request.getShowtimeId())
            .orElseThrow(() -> new AppException(ErrorCode.MOVIESCREEN_NOT_WORKING));

    for (String seatCode : request.getSeatCodes()) {
      if (ticketOrderRepository.countSeatBooked(movieScreen.getId(), seatCode) > 0) {
        log.error("Seat {} is already booked for showtime ID: {}", seatCode, movieScreen.getId());
        throw new AppException(ErrorCode.SEAT_ALREADY_BOOKED);
      }
      if (bookingCacheService.isSeatHeld(movieScreen.getId(), seatCode)) {
        log.error("Seat {} is already held for showtime ID: {}", seatCode, movieScreen.getId());
        throw new AppException(ErrorCode.SEAT_ALREADY_BOOKED);
      }
    }

    log.info("All seats are available for booking.");
    String ticketCode = generateTicketCode();
    log.info("Generated ticket code: {}", ticketCode);

    bookingCacheService.holdSeat(movieScreen.getId(), request.getSeatCodes(), ticketCode);

    BigDecimal ticketPrice =
        movieScreen.getTicketPrice().multiply(BigDecimal.valueOf(request.getSeatCodes().size()));
    log.info("Total ticket price: {}", ticketPrice);

    if (ticketPrice.compareTo(BigDecimal.ZERO) < 0) {
      log.error("Final price cannot be negative. Current value: {}", ticketPrice);
      throw new AppException(ErrorCode.INVALID_TICKET_PRICE);
    }

    bookingCacheService.saveTicketOrder(
        TicketOrderDTO.builder()
            .seatCode(request.getSeatCodes())
            .ticketCode(ticketCode)
            .userId(userId)
            .showtimeId(movieScreen.getId())
            .totalPrice(ticketPrice)
            .build());

    return buildBookingSeatResponse(request.getSeatCodes(), ticketCode, movieScreen, ticketPrice);
  }

  private BookingSeatResponse buildBookingSeatResponse(
      List<String> seatCodes, String ticketCode, MovieScreen movieScreen, BigDecimal price) {
    return BookingSeatResponse.builder()
        .ticketOrderCode(ticketCode)
        .seatCodes(seatCodes)
        .showtimeId(movieScreen.getId())
        .totalPrice(price)
        .cinemaId(movieScreen.getRoom().getCinema().getId())
        .cinemaName(movieScreen.getRoom().getCinema().getName())
        .showtimeTimeStart(movieScreen.getShowtime())
        .showtimeTimeEnd(
            movieScreen
                .getShowtime()
                .plusMinutes(movieScreen.getMovie().getDuration())
                .plusMinutes(15))
        .movieName(movieScreen.getMovie().getTitle())
        .roomId(movieScreen.getRoom().getId())
        .roomType(movieScreen.getRoom().getRoomType())
        .columnNumber(movieScreen.getRoom().getRoomType().getColumns())
        .roomNumber(movieScreen.getRoom().getRoomType().getColumns())
        .roomName(movieScreen.getRoom().getName())
        .build();
  }

  @Override
  public List<TicketHistoryResponse> getUserTicketHistory() {

    User user = getUserAuthenticated();

    // Sử dụng entity mapping thay vì Object[] query
    List<TicketOrder> ticketOrders =
        ticketOrderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

    List<TicketHistoryResponse> ticketHistory =
        ticketOrders.stream()
            .map(this::convertToTicketHistoryResponse)
            .collect(Collectors.toList());

    return ticketHistory;
  }

  private TicketHistoryResponse convertToTicketHistoryResponse(TicketOrder ticketOrder) {
    MovieScreen movieScreen = ticketOrder.getMovieScreen();
    Movie movie = movieScreen.getMovie();

    // Sửa từ getTicketOrderDetails() thành getTicketDetails()
    List<String> seatCodes =
        ticketOrder.getTicketDetails().stream() // ← Đây là method đúng
            .map(TicketOrderDetail::getSeatCode)
            .collect(Collectors.toList());

    // Tính toán showtime end
    LocalDateTime showtimeEnd =
        movieScreen.getShowtime().plusMinutes(movie.getDuration()).plusMinutes(15);

    return TicketHistoryResponse.builder()
        .ticketCode(ticketOrder.getTicketCode())
        .seatCodes(seatCodes)
        .totalPrice(ticketOrder.getTotalAmount())
        .paymentStatus(ticketOrder.getPaymentStatus().name())
        .bookingTime(ticketOrder.getCreatedAt())
        .movieTitle(movie.getTitle())
        .moviePosterUrl(movie.getThumbnailUrl())
        .movieDuration(movie.getDuration())
        .showtimeStart(movieScreen.getShowtime())
        .showtimeEnd(showtimeEnd)
        .cinemaName(movieScreen.getRoom().getCinema().getName())
        .roomName(movieScreen.getRoom().getName())
        .roomType(movieScreen.getRoom().getRoomType().name())
        .build();
  }

  private String generateTicketCode() {
    long timePart = System.currentTimeMillis() % 100000000;
    int randomPart = (int) (Math.random() * 90 + 10);
    return "TICKET_" + timePart + randomPart;
  }

  private User getUserAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication.getPrincipal() instanceof String) {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }
    return (User) authentication.getPrincipal();
  }
}
