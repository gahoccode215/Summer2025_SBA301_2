package com.sba301.online_ticket_sales.dto.dashboard;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CinemaPerformance {
  private Long cinemaId;
  private String cinemaName;
  private Long totalBookings;
  private BigDecimal totalRevenue;
  private Double occupancyRate;
}
