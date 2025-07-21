package com.sba301.online_ticket_sales.dto.booking.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "TicketOrderDTO", timeToLive = 900)
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TicketOrderDTO {
  @Id private String ticketCode;
  private Long userId;
  private Long showtimeId;
  private BigDecimal totalPrice;
  private List<String> seatCode;
}
