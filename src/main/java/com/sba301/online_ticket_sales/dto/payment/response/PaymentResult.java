package com.sba301.online_ticket_sales.dto.payment.response;

import com.sba301.online_ticket_sales.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResult {
    private boolean success;
    private String paymentUrl;
    private String transactionId;
    private String errorMessage;
    private PaymentStatus status;
}
