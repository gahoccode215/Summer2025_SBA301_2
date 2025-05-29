package com.sba301.online_ticket_sales.dto.cinema.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CinemaResponse {
  private Long id;
  private String name;
  private String hotline;
  private String address;
  private String province;
  private boolean isActive;
}
