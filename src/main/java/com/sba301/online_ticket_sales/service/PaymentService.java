package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.booking.request.VnPayCallbackParamRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
  String createPayment(String orderCode, HttpServletRequest request);

  void handlePaymentCallback(VnPayCallbackParamRequest request);
}
