package com.sba301.online_ticket_sales.dto.booking.response;

import com.sba301.online_ticket_sales.enums.RoomType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class BookingSeatResponse {
  private String ticketOrderCode;
  private List<String> seatCodes;
  private BigDecimal totalPrice;

  private Long cinemaId;
  private String cinemaName;

  private Long showtimeId;
  private LocalDateTime showtimeTimeStart;
  private LocalDateTime showtimeTimeEnd;

  private String movieName;

  private Long roomId;
  private RoomType roomType;
  private String roomName;
  private int roomNumber;
  private int columnNumber;

  private String rewardCode;
  private String rewardName;
  private BigDecimal rewardDiscountAmount;
}
