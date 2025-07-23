package com.sba301.online_ticket_sales.controller;

import com.sba301.online_ticket_sales.dto.booking.request.BookingTicketRequest;
import com.sba301.online_ticket_sales.dto.booking.request.TicketSearchRequest;
import com.sba301.online_ticket_sales.dto.booking.response.*;
import com.sba301.online_ticket_sales.dto.common.ApiResponseDTO;
import com.sba301.online_ticket_sales.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Booking Controller")
@RequestMapping("/api/v1/bookings")
public class BookingController {
  BookingService bookingService;

  @PostMapping
  @Operation(summary = "Book Seats", description = "Book seats for a movie showtime")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Seats booked successfully",
        content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input data or missing required fields",
        content = @Content)
  })
  public ResponseEntity<ApiResponseDTO<BookingSeatResponse>> bookSeats(
      @RequestBody @Valid BookingTicketRequest bookingTicketRequest) {
    log.info("Received request to book seats: {}", bookingTicketRequest);
    BookingSeatResponse response = bookingService.bookSeats(bookingTicketRequest);
    return ResponseEntity.ok(
        ApiResponseDTO.<BookingSeatResponse>builder()
            .code(HttpStatus.OK.value())
            .message("Seats booked successfully")
            .result(response)
            .build());
  }

  @GetMapping("/seat/{showtimeId}")
  @PreAuthorize("isAuthenticated()")
  @Operation(
      summary = "Get Seat Map",
      description = "Retrieve the seat map for a specific showtime by its ID")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved seat map",
        content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
    @ApiResponse(responseCode = "404", description = "Showtime not found", content = @Content)
  })
  public ResponseEntity<ApiResponseDTO<SeatMapResponse>> getMapSeat(@PathVariable Long showtimeId) {
    log.info("Received request to get seat map for showtime ID: {}", showtimeId);
    SeatMapResponse response = bookingService.getSeatMap(showtimeId);
    return ResponseEntity.ok(
        ApiResponseDTO.<SeatMapResponse>builder()
            .code(HttpStatus.OK.value())
            .message("")
            .result(response)
            .build());
  }

  @GetMapping("/my-history-tickets")
  @PreAuthorize("isAuthenticated()")
  @Operation(
      summary = "Get User Ticket History",
      description = "Retrieve the ticket purchase history for the authenticated user")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved ticket history",
        content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content)
  })
  public ResponseEntity<ApiResponseDTO<List<TicketHistoryResponse>>> getMyTicketHistory() {
    log.info("Received request to get user ticket history");

    List<TicketHistoryResponse> ticketHistory = bookingService.getUserTicketHistory();

    return ResponseEntity.ok(
        ApiResponseDTO.<List<TicketHistoryResponse>>builder()
            .code(HttpStatus.OK.value())
            .message("User ticket history retrieved successfully")
            .result(ticketHistory)
            .build());
  }

  @PostMapping("/manager/{cinemaId}/book")
  @Operation(
      summary = "Book Seats By Manager or Admin",
      description = "Book seats for a movie showtime")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Seats booked successfully",
        content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input data or missing required fields",
        content = @Content)
  })
  public ResponseEntity<ApiResponseDTO<BookingSeatResponse>> bookSeatsByManager(
      @RequestBody @Valid BookingTicketRequest bookingTicketRequest,
      @PathVariable Long cinemaId,
      @RequestParam Long customerId) {
    log.info("Received request to book seats by manager: {}", bookingTicketRequest);
    BookingSeatResponse response =
        bookingService.bookSeatsByManager(bookingTicketRequest, cinemaId, customerId);
    return ResponseEntity.ok(
        ApiResponseDTO.<BookingSeatResponse>builder()
            .code(HttpStatus.OK.value())
            .message("Seats booked successfully")
            .result(response)
            .build());
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @Operation(
      summary = "Get all tickets",
      description = "Get all tickets with optional search by ticket code (Admin/Manager only)")
  public ResponseEntity<Page<TicketListResponse>> getAllTickets(
      @RequestParam(required = false) String ticketCode,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "DESC") String sortDirection) {

    TicketSearchRequest searchRequest = new TicketSearchRequest();
    searchRequest.setTicketCode(ticketCode);
    searchRequest.setPage(page);
    searchRequest.setSize(size);
    searchRequest.setSortBy(sortBy);
    searchRequest.setSortDirection(sortDirection);

    Page<TicketListResponse> tickets = bookingService.getAllTickets(searchRequest);
    return ResponseEntity.ok(tickets);
  }

  @GetMapping("/cinema/{cinemaId}")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @Operation(
      summary = "Get tickets by cinema",
      description = "Get tickets for a specific cinema with optional search by ticket code")
  public ResponseEntity<Page<TicketListResponse>> getTicketsByCinema(
      @PathVariable Long cinemaId,
      @RequestParam(required = false) String ticketCode,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "DESC") String sortDirection) {

    TicketSearchRequest searchRequest = new TicketSearchRequest();
    searchRequest.setTicketCode(ticketCode);
    searchRequest.setPage(page);
    searchRequest.setSize(size);
    searchRequest.setSortBy(sortBy);
    searchRequest.setSortDirection(sortDirection);

    Page<TicketListResponse> tickets = bookingService.getTicketsByCinema(cinemaId, searchRequest);
    return ResponseEntity.ok(tickets);
  }

  @GetMapping("/cinema/{cinemaId}/today")
  //  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @Operation(
      summary = "Get today's tickets by cinema",
      description = "Get today's tickets for a specific cinema")
  public ResponseEntity<List<TicketListResponse>> getTodayTicketsByCinema(
      @PathVariable Long cinemaId) {
    List<TicketListResponse> tickets = bookingService.getTodayTicketsByCinema(cinemaId);
    return ResponseEntity.ok(tickets);
  }

  @GetMapping("/detail/{ticketCode}")
  //  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF', 'USER')")
  @Operation(
      summary = "Get ticket detail by code",
      description = "Get detailed information of a ticket by ticket code")
  public ResponseEntity<TicketDetailResponse> getTicketDetail(@PathVariable String ticketCode) {
    TicketDetailResponse ticketDetail = bookingService.getTicketDetail(ticketCode);
    return ResponseEntity.ok(ticketDetail);
  }

  @GetMapping("/detail/id/{ticketId}")
  //  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @Operation(
      summary = "Get ticket detail by ID",
      description = "Get detailed information of a ticket by ticket ID")
  public ResponseEntity<TicketDetailResponse> getTicketDetailById(@PathVariable Long ticketId) {
    TicketDetailResponse ticketDetail = bookingService.getTicketDetailById(ticketId);
    return ResponseEntity.ok(ticketDetail);
  }

  @PostMapping("/checkin/{ticketCode}")
  //  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @Operation(summary = "Check-in ticket", description = "Check-in a ticket for movie entry")
  public ResponseEntity<CheckInResponse> checkInTicket(@PathVariable String ticketCode) {
    CheckInResponse response = bookingService.checkInTicket(ticketCode);
    return ResponseEntity.ok(response);
  }
}
