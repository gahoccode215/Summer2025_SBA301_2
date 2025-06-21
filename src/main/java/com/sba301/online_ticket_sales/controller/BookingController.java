package com.sba301.online_ticket_sales.controller;

import com.sba301.online_ticket_sales.dto.booking.request.BookingTicketRequest;
import com.sba301.online_ticket_sales.dto.booking.response.BookingSeatResponse;
import com.sba301.online_ticket_sales.dto.booking.response.SeatMapResponse;
import com.sba301.online_ticket_sales.dto.common.ApiResponseDTO;
import com.sba301.online_ticket_sales.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
}
