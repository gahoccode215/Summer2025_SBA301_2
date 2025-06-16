package com.sba301.online_ticket_sales.dto.payment.request;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class PaymentVerificationRequest {
    private String transactionId;
    private String secureHash;
    private String transactionStatus;
    private Map<String, String> parameters;
}
