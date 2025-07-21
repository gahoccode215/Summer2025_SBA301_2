package com.sba301.online_ticket_sales.dto.booking.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketHistoryResponse {
  private String ticketCode;
  private List<String> seatCodes;
  private BigDecimal totalPrice;
  private String paymentStatus;
  private LocalDateTime bookingTime;

  // Movie information
  private String movieTitle;
  private String moviePosterUrl;
  private Integer movieDuration;

  // Showtime information
  private LocalDateTime showtimeStart;
  private LocalDateTime showtimeEnd;

  // Cinema information
  private String cinemaName;
  private String roomName;
  private String roomType;
}
