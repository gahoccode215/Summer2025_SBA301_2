package com.sba301.online_ticket_sales.dto.payment.response;

import com.sba301.online_ticket_sales.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentVerificationResult {
    private boolean valid;
    private PaymentStatus status;
    private String transactionId;
    private String errorMessage;
}
