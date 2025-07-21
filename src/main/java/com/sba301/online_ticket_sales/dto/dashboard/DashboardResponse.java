package com.sba301.online_ticket_sales.dto.dashboard;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class DashboardResponse {
    private DashboardOverview overview;
    private List<TopMovie> topMovies;
    private List<RevenueByDate> revenueChart;
    private List<CinemaPerformance> cinemaPerformances;
    private List<RecentBooking> recentBookings;
}
