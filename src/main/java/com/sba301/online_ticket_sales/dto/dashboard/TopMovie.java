package com.sba301.online_ticket_sales.dto.dashboard;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Data
@Builder
public class TopMovie {
    private Long movieId;
    private String movieTitle;
    private String thumbnailUrl;
    private Long totalBookings;
    private BigDecimal totalRevenue;
    private Double rating;
}
