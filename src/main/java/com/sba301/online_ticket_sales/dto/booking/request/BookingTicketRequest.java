package com.sba301.online_ticket_sales.dto.booking.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class BookingTicketRequest {
  @NotNull(message = "Showtime ID cannot be null")
  private Long showtimeId;

  @NotNull(message = "Seat codes cannot be null")
  private List<String> seatCodes;
}
