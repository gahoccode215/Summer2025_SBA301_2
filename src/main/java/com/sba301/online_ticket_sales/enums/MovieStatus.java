package com.sba301.online_ticket_sales.enums;

import lombok.Getter;

@Getter
public enum MovieStatus {
//  @JsonProperty("PHIM SẮP CHIẾU")
  UPCOMING,
//  @JsonProperty("PHIM ĐANG CHIẾU")
  NOW_SHOWING,
//  @JsonProperty("NGƯNG CHIẾU")
  ENDED,
//  @JsonProperty("PHIM IMAX")
  IMAX;
}
