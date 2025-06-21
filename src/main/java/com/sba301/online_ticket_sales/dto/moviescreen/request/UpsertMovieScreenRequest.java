package com.sba301.online_ticket_sales.dto.moviescreen.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class UpsertMovieScreenRequest {
  @NotNull(message = "Movie ID cannot be null")
  private Long movieId;

  @NotNull(message = "Room ID cannot be null")
  private Long roomId;

  @NotNull(message = "Showtime cannot be null")
  private LocalDateTime showtime;

  @NotNull(message = "Ticket price cannot be null")
  @DecimalMin(
      value = "0.0",
      inclusive = true,
      message = "Ticket price must be greater than or equal to 0")
  private BigDecimal ticketPrice;
}
