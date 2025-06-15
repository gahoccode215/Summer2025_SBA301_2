package com.sba301.online_ticket_sales.dto.cinema.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CinemaDetailResponse {
  private Long id;
  private String name;
  private String hotline;
  private String address;
  private String province;
  private List<RoomResponse> roomResponseList;
  private boolean isActive;
  private String imageUrl;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private List<TickerPriceResponse> ticketPrices;
}
