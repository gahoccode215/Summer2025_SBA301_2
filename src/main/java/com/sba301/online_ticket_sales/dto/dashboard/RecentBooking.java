package com.sba301.online_ticket_sales.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecentBooking {
  private String ticketCode;
  private String customerName;
  private String movieTitle;
  private String cinemaName;
  private LocalDateTime bookingTime;
  private BigDecimal totalAmount;
  private String paymentStatus;
}
