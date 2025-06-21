package com.sba301.online_ticket_sales.dto.moviescreen.response;

import java.util.List;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class CinemaShowTimeResponse {
  private Long cinemaId;
  private String cinemaName;
  private String cinemaAddress;
  private List<ShowTimeResponse> showTimes;
}
