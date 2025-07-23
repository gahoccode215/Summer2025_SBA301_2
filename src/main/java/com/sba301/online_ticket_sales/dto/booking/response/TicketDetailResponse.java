package com.sba301.online_ticket_sales.dto.booking.response;

import com.sba301.online_ticket_sales.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketDetailResponse {
  private Long ticketId;
  private String ticketCode;

  // Customer info
  private Long customerId;
  private String customerName;
  private String customerEmail;
  private String customerPhone;

  // Movie info
  private Long movieId;
  private String movieTitle;
  private String moviePosterUrl;
  private Integer movieDuration;

  // Cinema & Room info
  private Long cinemaId;
  private String cinemaName;
  private String cinemaAddress;
  private Long roomId;
  private String roomName;
  private String roomType;

  // Showtime info
  private Long showtimeId;
  private LocalDateTime showtimeStart;
  private LocalDateTime showtimeEnd;

  // Ticket details
  private List<SeatDetail> seatDetails;
  private BigDecimal totalAmount;
  private PaymentStatus paymentStatus;
  private boolean isPrinted;
  private boolean isCheckedIn;
  private LocalDateTime checkInTime;
  private String checkInBy;
  private LocalDateTime bookingTime;
  private LocalDateTime lastUpdated;
}
