package com.sba301.online_ticket_sales.dto.dashboard;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
