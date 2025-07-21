package com.sba301.online_ticket_sales.dto.booking.request;

import lombok.*;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VnPayCallbackParamRequest {
  private String vnp_Amount;
  private String vnp_BankCode;
  private String vnp_BankTranNo;
  private String vnp_CardType;
  private String vnp_OrderInfo;
  private String vnp_PayDate;
  private String vnp_ResponseCode;
  private String vnp_TmnCode;
  private String vnp_TransactionNo;
  private String vnp_TransactionStatus;
  private String vnp_TxnRef;
  private String vnp_SecureHash;

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }
}
