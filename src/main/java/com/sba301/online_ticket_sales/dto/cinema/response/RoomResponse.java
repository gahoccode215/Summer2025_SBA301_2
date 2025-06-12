package com.sba301.online_ticket_sales.dto.cinema.response;

import com.sba301.online_ticket_sales.enums.RoomType;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
public class RoomResponse {
  private Long id;
  private String name;
  private RoomType roomType;
  private Integer seatCount;
  private Integer rowCount;
  private boolean isActive;
}
