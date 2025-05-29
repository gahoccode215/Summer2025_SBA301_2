package com.sba301.online_ticket_sales.exception;

import com.sba301.online_ticket_sales.enums.ErrorCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppException extends RuntimeException {

  public AppException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  private ErrorCode errorCode;
}
