package com.sba301.online_ticket_sales.enums;

import lombok.Getter;

@Getter
public enum Occupation {
  DIRECTOR("Đạo diễn"),
  ACTOR("Diễn viên");

  private final String displayName;

  Occupation(String displayName) {
    this.displayName = displayName;
  }
}
