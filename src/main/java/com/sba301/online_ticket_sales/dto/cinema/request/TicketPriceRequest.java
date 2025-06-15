package com.sba301.online_ticket_sales.dto.cinema.request;

import com.sba301.online_ticket_sales.enums.DateType;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class TicketPriceRequest {
  private Long id;
  private DateType dateType;
  private BigDecimal price;
}
