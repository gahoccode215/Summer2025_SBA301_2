package com.sba301.online_ticket_sales.dto.payment.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentRequest {
    private Long bookingId;
    private BigDecimal amount;
    private String orderInfo;
    private String returnUrl;
    private String ipAddress;
    private String locale;
}
