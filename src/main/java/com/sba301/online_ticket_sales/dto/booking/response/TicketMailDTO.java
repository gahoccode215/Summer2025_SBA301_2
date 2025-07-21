package com.sba301.online_ticket_sales.dto.booking.response;

import com.sba301.online_ticket_sales.enums.RoomType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class TicketMailDTO {
  private String email;
  private String ticketCode;
  private String cinemaName;
  private String cinemaAddress;
  private String roomName;
  private RoomType roomType;
  private String movieName;
  private LocalDateTime showtimeStartTime;
  private LocalDateTime showtimeEndTime;
  private List<String> seatCodes;
  private BigDecimal totalPrice;
}
