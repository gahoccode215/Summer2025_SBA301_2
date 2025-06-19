package com.sba301.online_ticket_sales.dto.booking.response;

import com.sba301.online_ticket_sales.dto.moviescreen.response.MovieScreenResponse;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetailResponse {
  private Long id;

  private String seatNumber;

  private BigDecimal ticketPrice;

  private MovieScreenResponse movieScreen;
}
