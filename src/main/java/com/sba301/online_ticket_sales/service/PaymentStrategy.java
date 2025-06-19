package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.payment.request.PaymentRequest;
import com.sba301.online_ticket_sales.dto.payment.request.PaymentVerificationRequest;
import com.sba301.online_ticket_sales.dto.payment.response.PaymentResult;
import com.sba301.online_ticket_sales.dto.payment.response.PaymentVerificationResult;

public interface PaymentStrategy {
  PaymentResult createPayment(PaymentRequest request);

  PaymentVerificationResult verifyPayment(PaymentVerificationRequest request);

  String getPaymentMethod();
}
