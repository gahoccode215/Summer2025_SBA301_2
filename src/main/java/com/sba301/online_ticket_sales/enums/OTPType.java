package com.sba301.online_ticket_sales.enums;

public enum OTPType {
  FORGOT_PASSWORD,
  REGISTER;

  public boolean isRegister() {
    return this == REGISTER;
  }

  public boolean isForgotPassword() {
    return this == FORGOT_PASSWORD;
  }
}
