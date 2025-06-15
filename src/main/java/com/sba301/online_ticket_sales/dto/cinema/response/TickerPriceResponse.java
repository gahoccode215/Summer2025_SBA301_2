package com.sba301.online_ticket_sales.dto.cinema.response;

import com.sba301.online_ticket_sales.enums.DateType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class TickerPriceResponse {
  private Long priceId;
  private DateType dateType;
  private BigDecimal price;
  private LocalDateTime createAt;
  private boolean isActive;
}
