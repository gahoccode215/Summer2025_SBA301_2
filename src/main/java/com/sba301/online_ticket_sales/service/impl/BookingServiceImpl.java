package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.booking.request.BookingTicketRequest;
import com.sba301.online_ticket_sales.dto.booking.request.TicketSearchRequest;
import com.sba301.online_ticket_sales.dto.booking.response.*;
import com.sba301.online_ticket_sales.entity.*;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.enums.PaymentStatus;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.repository.*;
import com.sba301.online_ticket_sales.service.BookingCacheService;
import com.sba301.online_ticket_sales.service.BookingService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

  private final MovieScreenRepository movieScreenRepository;
  private final BookingCacheService bookingCacheService;
  private final TicketOrderRepository ticketOrderRepository;
  private final UserRepository userRepository;

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
  @Transactional
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

    MovieScreen movieScreen =
        movieScreenRepository
            .findById(bookingTicketRequest.getShowtimeId())
            .orElseThrow(() -> new AppException(ErrorCode.MOVIESCREEN_NOT_WORKING));

    for (String seatCode : bookingTicketRequest.getSeatCodes()) {
      if (ticketOrderRepository.countSeatBooked(movieScreen.getId(), seatCode) > 0) {
        log.error("Seat {} is already booked for showtime ID: {}", seatCode, movieScreen.getId());
        throw new AppException(ErrorCode.SEAT_ALREADY_BOOKED);
      }
    }

    log.info("All seats are available for booking.");
    String ticketCode = generateTicketCode();
    log.info("Generated ticket code: {}", ticketCode);

    BigDecimal totalPrice =
        movieScreen
            .getTicketPrice()
            .multiply(BigDecimal.valueOf(bookingTicketRequest.getSeatCodes().size()));

    if (totalPrice.compareTo(BigDecimal.ZERO) < 0) {
      log.error("Final price cannot be negative. Current value: {}", totalPrice);
      throw new AppException(ErrorCode.INVALID_TICKET_PRICE);
    }

    User customer =
        userRepository
            .findById(customerId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

    BigDecimal ticketPrice =
        totalPrice.divide(
            BigDecimal.valueOf(bookingTicketRequest.getSeatCodes().size()),
            2,
            RoundingMode.HALF_UP);

    TicketOrder ticketOrder = new TicketOrder();
    ticketOrder.setTicketCode(ticketCode);
    ticketOrder.setUser(customer);
    ticketOrder.setMovieScreen(movieScreen);
    ticketOrder.setTotalAmount(totalPrice);
    ticketOrder.setPaymentStatus(PaymentStatus.SUCCESS);

    List<TicketOrderDetail> details = new ArrayList<>();
    for (String seatCode : bookingTicketRequest.getSeatCodes()) {
      TicketOrderDetail detail = new TicketOrderDetail();
      detail.setSeatCode(seatCode);
      detail.setPrice(ticketPrice);
      detail.setTicketOrder(ticketOrder);
      details.add(detail);
    }
    ticketOrder.setTicketDetails(details);

    ticketOrderRepository.save(ticketOrder);

    return buildBookingSeatResponse(
        bookingTicketRequest.getSeatCodes(), ticketCode, movieScreen, totalPrice);
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

  @Override
  public Page<TicketListResponse> getAllTickets(TicketSearchRequest searchRequest) {
    log.info("Getting all tickets with search criteria: {}", searchRequest);

    User user = getUserAuthenticated();
    List<String> roleNames =
            user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

    boolean isAdmin =
            roleNames.contains("ADMIN") || roleNames.contains("ROLE_ADMIN");

    boolean isManagerOrStaff =
            roleNames.contains("MANAGER") || roleNames.contains("ROLE_MANAGER") ||
                    roleNames.contains("STAFF") || roleNames.contains("ROLE_STAFF");

    if (!isAdmin && !isManagerOrStaff) {
      throw new AppException(ErrorCode.NO_PERMISSION_TO_VIEW_TICKETS);
    }

    Pageable pageable = createPageable(searchRequest);
    Page<TicketOrder> ticketOrders;

    if (isAdmin) {
      // Admin can see all tickets
      ticketOrders = ticketOrderRepository.findAllTicketsWithSearch(
              searchRequest.getTicketCode(), pageable);
    } else {
      // Manager/Staff can only see tickets from their managed cinemas
      List<Long> managedCinemaIds = user.getManagedCinemas().stream()
              .map(Cinema::getId)
              .toList();

      if (managedCinemaIds.isEmpty()) {
        throw new AppException(ErrorCode.NO_PERMISSION_TO_VIEW_TICKETS);
      }

      ticketOrders = ticketOrderRepository.findTicketsByManagedCinemasWithSearch(
              managedCinemaIds, searchRequest.getTicketCode(), pageable);
    }

    return ticketOrders.map(this::convertToTicketListResponse);
  }

  @Override
  public Page<TicketListResponse> getTicketsByCinema(
          Long cinemaId, TicketSearchRequest searchRequest) {
    log.info("Getting tickets for cinema ID: {} with search criteria: {}", cinemaId, searchRequest);

    User user = getUserAuthenticated();
    List<String> roleNames =
            user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

    boolean isAdmin =
            roleNames.contains("ADMIN") || roleNames.contains("ROLE_ADMIN");

    boolean isManagerOrStaff =
            roleNames.contains("MANAGER") || roleNames.contains("ROLE_MANAGER") ||
                    roleNames.contains("STAFF") || roleNames.contains("ROLE_STAFF");

    if (!isAdmin && !isManagerOrStaff) {
      throw new AppException(ErrorCode.NO_PERMISSION_TO_VIEW_TICKETS);
    }

    // Check cinema access for non-admin users
    if (!isAdmin) {
      boolean hasAccess = user.getManagedCinemas().stream()
              .anyMatch(cinema -> cinema.getId().equals(cinemaId));
      if (!hasAccess) {
        throw new AppException(ErrorCode.NO_PERMISSION_TO_VIEW_TICKETS);
      }
    }

    Pageable pageable = createPageable(searchRequest);

    Page<TicketOrder> ticketOrders =
            ticketOrderRepository.findTicketsByCinemaWithSearch(
                    cinemaId, searchRequest.getTicketCode(), pageable);

    return ticketOrders.map(this::convertToTicketListResponse);
  }

  @Override
  public TicketDetailResponse getTicketDetail(String ticketCode) {
    log.info("Getting ticket detail for ticket code: {}", ticketCode);

    TicketOrder ticketOrder =
        ticketOrderRepository
            .findByTicketCodeWithDetails(ticketCode)
            .orElseThrow(() -> new AppException(ErrorCode.TICKET_NOT_FOUND));

    validateTicketAccess(ticketOrder);

    return convertToTicketDetailResponse(ticketOrder);
  }

  @Override
  public TicketDetailResponse getTicketDetailById(Long ticketId) {
    log.info("Getting ticket detail for ticket ID: {}", ticketId);

    TicketOrder ticketOrder =
        ticketOrderRepository
            .findByIdWithDetails(ticketId)
            .orElseThrow(() -> new AppException(ErrorCode.TICKET_NOT_FOUND));

    validateTicketAccess(ticketOrder);

    return convertToTicketDetailResponse(ticketOrder);
  }

  @Override
  @Transactional
  public CheckInResponse checkInTicket(String ticketCode) {
    log.info("Checking in ticket with code: {}", ticketCode);

    User user = getUserAuthenticated();
    List<String> roleNames =
        user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

    boolean isStaff =
        roleNames.contains("STAFF")
            || roleNames.contains("ROLE_STAFF")
            || roleNames.contains("MANAGER")
            || roleNames.contains("ROLE_MANAGER")
            || roleNames.contains("ADMIN")
            || roleNames.contains("ROLE_ADMIN");

    if (!isStaff) {
      throw new AppException(ErrorCode.NO_PERMISSION_TO_CHECKIN);
    }

    TicketOrder ticketOrder =
        ticketOrderRepository
            .findByTicketCodeWithDetails(ticketCode)
            .orElseThrow(() -> new AppException(ErrorCode.TICKET_NOT_FOUND));

    // Validate ticket status
    if (ticketOrder.getPaymentStatus() != PaymentStatus.SUCCESS) {
      throw new AppException(ErrorCode.TICKET_NOT_PAID);
    }

    if (ticketOrder.isCheckedIn()) {
      throw new AppException(ErrorCode.TICKET_ALREADY_CHECKED_IN);
    }

    // Check if showtime is today and within check-in window (30 minutes before showtime)
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime showtimeStart = ticketOrder.getMovieScreen().getShowtime();
    LocalDateTime checkInWindow = showtimeStart.minusMinutes(30);

//    if (now.isBefore(checkInWindow)) {
//      throw new AppException(ErrorCode.CHECKIN_TOO_EARLY);
//    }
//
//    if (now.isAfter(showtimeStart.plusMinutes(15))) {
//      throw new AppException(ErrorCode.CHECKIN_TOO_LATE);
//    }

    // Check cinema access for non-admin users
    boolean isAdmin =
        roleNames.contains("MANAGER")
            || roleNames.contains("ROLE_MANAGER")
            || roleNames.contains("ADMIN")
            || roleNames.contains("ROLE_ADMIN");

    if (!isAdmin) {
      Long cinemaId = ticketOrder.getMovieScreen().getRoom().getCinema().getId();
      boolean hasAccess =
          user.getManagedCinemas().stream().anyMatch(cinema -> cinema.getId().equals(cinemaId));
      if (!hasAccess) {
        throw new AppException(ErrorCode.NO_PERMISSION_TO_CHECKIN);
      }
    }

    // Perform check-in
    ticketOrder.setCheckedIn(true);
    ticketOrder.setCheckInTime(now);
    ticketOrder.setCheckInBy(user.getUsername());

    ticketOrderRepository.save(ticketOrder);

    log.info("Ticket {} checked in successfully by {}", ticketCode, user.getUsername());

    List<String> seatCodes =
        ticketOrder.getTicketDetails().stream().map(TicketOrderDetail::getSeatCode).toList();

    return CheckInResponse.builder()
        .ticketCode(ticketCode)
        .customerName(ticketOrder.getUser().getFullName())
        .movieTitle(ticketOrder.getMovieScreen().getMovie().getTitle())
        .cinemaName(ticketOrder.getMovieScreen().getRoom().getCinema().getName())
        .roomName(ticketOrder.getMovieScreen().getRoom().getName())
        .showtimeStart(showtimeStart)
        .seatCodes(seatCodes)
        .checkInTime(now)
        .checkInBy(user.getUsername())
        .message("Ticket checked in successfully")
        .build();
  }

  @Override
  public List<TicketListResponse> getTodayTicketsByCinema(Long cinemaId) {
    log.info("Getting today's tickets for cinema ID: {}", cinemaId);

    User user = getUserAuthenticated();
    List<String> roleNames =
        user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

    boolean isAdmin =
        roleNames.contains("MANAGER")
            || roleNames.contains("ROLE_MANAGER")
            || roleNames.contains("ADMIN")
            || roleNames.contains("ROLE_ADMIN");

    if (!isAdmin) {
      boolean hasAccess =
          user.getManagedCinemas().stream().anyMatch(cinema -> cinema.getId().equals(cinemaId));
      if (!hasAccess) {
        throw new AppException(ErrorCode.NO_PERMISSION_TO_VIEW_TICKETS);
      }
    }

    LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
    LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

    List<TicketOrder> ticketOrders =
        ticketOrderRepository.findTicketsByCinemaAndTimeRange(
            List.of(cinemaId), startOfDay, endOfDay);

    return ticketOrders.stream().map(this::convertToTicketListResponse).toList();
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

  private void validateTicketAccess(TicketOrder ticketOrder) {
    User user = getUserAuthenticated();
    List<String> roleNames =
        user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

    boolean isAdmin =
        roleNames.contains("MANAGER")
            || roleNames.contains("ROLE_MANAGER")
            || roleNames.contains("ADMIN")
            || roleNames.contains("ROLE_ADMIN")
            || roleNames.contains("STAFF")
            || roleNames.contains("ROLE_STAFF");

    if (!isAdmin) {
      // Check if user is the ticket owner
      if (!ticketOrder.getUser().getId().equals(user.getId())) {
        // Check if user has access to the cinema
        Long cinemaId = ticketOrder.getMovieScreen().getRoom().getCinema().getId();
        boolean hasAccess =
            user.getManagedCinemas().stream().anyMatch(cinema -> cinema.getId().equals(cinemaId));
        if (!hasAccess) {
          throw new AppException(ErrorCode.NO_PERMISSION_TO_VIEW_TICKET);
        }
      }
    }
  }

  private TicketListResponse convertToTicketListResponse(TicketOrder ticketOrder) {
    MovieScreen movieScreen = ticketOrder.getMovieScreen();
    Movie movie = movieScreen.getMovie();

    List<String> seatCodes =
        ticketOrder.getTicketDetails().stream().map(TicketOrderDetail::getSeatCode).toList();

    LocalDateTime showtimeEnd =
        movieScreen.getShowtime().plusMinutes(movie.getDuration()).plusMinutes(15);

    return TicketListResponse.builder()
        .ticketId(ticketOrder.getId())
        .ticketCode(ticketOrder.getTicketCode())
        .customerName(ticketOrder.getUser().getFullName())
        .customerEmail(ticketOrder.getUser().getEmail())
        .movieTitle(movie.getTitle())
        .cinemaName(movieScreen.getRoom().getCinema().getName())
        .roomName(movieScreen.getRoom().getName())
        .showtimeStart(movieScreen.getShowtime())
        .showtimeEnd(showtimeEnd)
        .seatCodes(seatCodes)
        .totalAmount(ticketOrder.getTotalAmount())
        .paymentStatus(ticketOrder.getPaymentStatus())
        .isPrinted(ticketOrder.isPrinted())
        .isCheckedIn(ticketOrder.isCheckedIn())
        .bookingTime(ticketOrder.getCreatedAt())
        .build();
  }

  private TicketDetailResponse convertToTicketDetailResponse(TicketOrder ticketOrder) {
    MovieScreen movieScreen = ticketOrder.getMovieScreen();
    Movie movie = movieScreen.getMovie();
    Room room = movieScreen.getRoom();
    Cinema cinema = room.getCinema();
    User customer = ticketOrder.getUser();

    List<SeatDetail> seatDetails =
        ticketOrder.getTicketDetails().stream()
            .map(
                detail ->
                    SeatDetail.builder()
                        .seatCode(detail.getSeatCode())
                        .price(detail.getPrice())
                        .build())
            .toList();

    LocalDateTime showtimeEnd =
        movieScreen.getShowtime().plusMinutes(movie.getDuration()).plusMinutes(15);

    return TicketDetailResponse.builder()
        .ticketId(ticketOrder.getId())
        .ticketCode(ticketOrder.getTicketCode())
        .customerId(customer.getId())
        .customerName(customer.getFullName())
        .customerEmail(customer.getEmail())
        .customerPhone(customer.getPhone())
        .movieId(movie.getId())
        .movieTitle(movie.getTitle())
        .moviePosterUrl(movie.getThumbnailUrl())
        .movieDuration(movie.getDuration())
        .cinemaId(cinema.getId())
        .cinemaName(cinema.getName())
        .cinemaAddress(cinema.getAddress())
        .roomId(room.getId())
        .roomName(room.getName())
        .roomType(room.getRoomType().name())
        .showtimeId(movieScreen.getId())
        .showtimeStart(movieScreen.getShowtime())
        .showtimeEnd(showtimeEnd)
        .seatDetails(seatDetails)
        .totalAmount(ticketOrder.getTotalAmount())
        .paymentStatus(ticketOrder.getPaymentStatus())
        .isPrinted(ticketOrder.isPrinted())
        .isCheckedIn(ticketOrder.isCheckedIn())
        .checkInTime(ticketOrder.getCheckInTime())
        .checkInBy(ticketOrder.getCheckInBy())
        .bookingTime(ticketOrder.getCreatedAt())
        .lastUpdated(ticketOrder.getUpdatedAt())
        .build();
  }

  private Pageable createPageable(TicketSearchRequest searchRequest) {
    Sort sort =
        searchRequest.getSortDirection().equalsIgnoreCase("ASC")
            ? Sort.by(searchRequest.getSortBy()).ascending()
            : Sort.by(searchRequest.getSortBy()).descending();

    return PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), sort);
  }
}
