package com.sba301.online_ticket_sales.dto.dashboard;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

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
