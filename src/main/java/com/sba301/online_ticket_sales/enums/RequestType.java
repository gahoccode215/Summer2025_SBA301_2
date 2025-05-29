package com.sba301.online_ticket_sales.enums;

public enum RequestType {
  CREATE,
  UPDATE;

  public boolean isCreate() {
    return this == CREATE;
  }

  public boolean isUpdate() {
    return this == UPDATE;
  }
}
