package com.sba301.online_ticket_sales.enums;

import lombok.Getter;

@Getter
public enum MovieStatus {
  UPCOMING("PHIM SẮP CHIẾU"),
  NOW_SHOWING("PHIM ĐANG CHIẾU"),
  ENDED("NGƯNG CHIẾU"),
  IMAX("PHIM IMAX");

  private final String displayName;

  MovieStatus(String displayName) {
    this.displayName = displayName;
  }
}
