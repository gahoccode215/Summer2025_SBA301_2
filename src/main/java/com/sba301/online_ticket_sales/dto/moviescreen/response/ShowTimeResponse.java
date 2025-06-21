package com.sba301.online_ticket_sales.dto.moviescreen.response;

import com.sba301.online_ticket_sales.enums.RoomType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ShowTimeResponse {
  private Long showTimeId;
  private Long roomId;
  private LocalDateTime showTime;
  private RoomType roomType;
}
