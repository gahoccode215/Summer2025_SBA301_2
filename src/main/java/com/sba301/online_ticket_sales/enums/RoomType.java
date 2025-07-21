package com.sba301.online_ticket_sales.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RoomType {
  STANDARD(6, 6, 100),
  VIP(7, 5, 120),
  IMAX(7, 6, 150);

  private final int rows;
  private final int columns;
  private final int priceRate;
}
