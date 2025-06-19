package com.sba301.online_ticket_sales.dto.payment.request;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentVerificationRequest {
  private String transactionId;
  private String secureHash;
  private String transactionStatus;
  private Map<String, String> parameters;
}
