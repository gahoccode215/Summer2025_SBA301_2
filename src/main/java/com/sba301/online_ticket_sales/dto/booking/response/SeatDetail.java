package com.sba301.online_ticket_sales.dto.booking.response;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeatDetail {
  private String seatCode;
  private BigDecimal price;
}
