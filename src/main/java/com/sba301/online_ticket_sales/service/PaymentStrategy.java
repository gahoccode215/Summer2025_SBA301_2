package com.sba301.online_ticket_sales.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

public interface PaymentStrategy {
  String hmacSHA512(String data);

  Map<String, String> buildVnPayParams(Long amount, String orderCode, HttpServletRequest request);

  String getPaymentUrl(String queryUrl);
}
