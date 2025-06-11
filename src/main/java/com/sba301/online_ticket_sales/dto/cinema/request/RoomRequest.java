package com.sba301.online_ticket_sales.dto.cinema.request;

import com.sba301.online_ticket_sales.enums.RequestType;
import com.sba301.online_ticket_sales.enums.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequest {
  private Long id;

  @NotNull(message = "Name cannot be null")
  @NotBlank(message = "Name cannot be blank")
  private String name;

  private RoomType roomType;

    @NotNull(message = "seatCount type cannot be null")
  private Integer seatCount;
  @NotNull(message = "rowCount type cannot be null")
    private Integer rowCount;

  private RequestType requestType;
}
