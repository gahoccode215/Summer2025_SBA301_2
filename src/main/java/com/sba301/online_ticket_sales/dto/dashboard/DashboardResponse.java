package com.sba301.online_ticket_sales.dto.dashboard;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {
  private DashboardOverview overview;
  private List<TopMovie> topMovies;
  private List<RevenueByDate> revenueChart;
  private List<CinemaPerformance> cinemaPerformances;
  private List<RecentBooking> recentBookings;
}
