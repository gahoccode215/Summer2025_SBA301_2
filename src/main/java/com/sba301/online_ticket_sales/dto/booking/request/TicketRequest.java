package com.sba301.online_ticket_sales.dto.booking.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest {
  @NotNull(message = "ID suất chiếu không được để trống")
  private Long movieScreenId;

  @NotBlank(message = "Số ghế không được để trống")
  private String seatNumber;
}
