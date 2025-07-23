package com.sba301.online_ticket_sales.dto.booking.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckInResponse {
  private String ticketCode;
  private String customerName;
  private String movieTitle;
  private String cinemaName;
  private String roomName;
  private LocalDateTime showtimeStart;
  private List<String> seatCodes;
  private LocalDateTime checkInTime;
  private String checkInBy;
  private String message;
}
