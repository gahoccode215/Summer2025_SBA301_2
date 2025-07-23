package com.sba301.online_ticket_sales.dto.booking.response;

import com.sba301.online_ticket_sales.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketListResponse {
  private Long ticketId;
  private String ticketCode;
  private String customerName;
  private String customerEmail;
  private String movieTitle;
  private String cinemaName;
  private String roomName;
  private LocalDateTime showtimeStart;
  private LocalDateTime showtimeEnd;
  private List<String> seatCodes;
  private BigDecimal totalAmount;
  private PaymentStatus paymentStatus;
  private boolean isPrinted;
  private boolean isCheckedIn;
  private LocalDateTime bookingTime;
}
