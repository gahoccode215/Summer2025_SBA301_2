package com.sba301.online_ticket_sales.dto.common;

import com.sba301.online_ticket_sales.enums.OTPType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OTPMailDTO {
  private String receiverMail;
  private String otpCode;
  private OTPType type;
}
