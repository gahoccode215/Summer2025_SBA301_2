package com.sba301.online_ticket_sales.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RevenueByDate {
  private LocalDate date;
  private BigDecimal revenue;
  private Long bookings;
}
