package com.sba301.online_ticket_sales.dto.dashboard;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Data
@Builder
public class CinemaPerformance {
    private Long cinemaId;
    private String cinemaName;
    private Long totalBookings;
    private BigDecimal totalRevenue;
    private Double occupancyRate;
}
