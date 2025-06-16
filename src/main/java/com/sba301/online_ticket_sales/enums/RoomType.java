package com.sba301.online_ticket_sales.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RoomType {
  STANDARD(10, 15, 100),
  VIP(8, 12, 120),
  IMAX(12, 20, 150);

  private final int rows;
  private final int columns;
  private final int priceRate;
}
