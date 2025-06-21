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
public class SeatMapResponse {
  private Long cinemaId;
  private String cinemaName;

  private Long showtimeId;
  private LocalDateTime showtimeTimeStart;
  private LocalDateTime showtimeTimeEnd;

  private Long movieId;
  private String movieName;
  private String moviePosterUrl;

  private Long roomId;
  private RoomType roomType;
  private String roomName;

  private BigDecimal ticketPrice;

  private Integer totalSeatBooked;
  private List<String> bookedSeats;
}
