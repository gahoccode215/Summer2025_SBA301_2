package com.sba301.online_ticket_sales.dto.dashboard;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardOverview {
  private Long totalMovies;
  private Long totalCinemas;
  private Long totalActiveShowtimes;
  private Long totalBookings;
  private BigDecimal totalRevenue;
  private BigDecimal todayRevenue;
  private Long todayBookings;
  private Double averageRating;
}
