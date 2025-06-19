package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.config.VNPayConfig;
import com.sba301.online_ticket_sales.dto.payment.request.PaymentRequest;
import com.sba301.online_ticket_sales.dto.payment.request.PaymentVerificationRequest;
import com.sba301.online_ticket_sales.dto.payment.response.PaymentResult;
import com.sba301.online_ticket_sales.dto.payment.response.PaymentVerificationResult;
import com.sba301.online_ticket_sales.enums.PaymentStatus;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class VNPayStrategy implements PaymentStrategy {
  private static final String PAYMENT_METHOD = "VNPAY";
  private static final String VERSION = "2.1.0";
  private static final String COMMAND = "pay";
  private static final String ORDER_TYPE = "billpayment";
  private static final String CURRENCY_CODE = "VND";
  private static final int PAYMENT_TIMEOUT_MINUTES = 15;

  @Override
  public PaymentResult createPayment(PaymentRequest request) {
    try {
      String paymentUrl = buildPaymentUrl(request);

      return PaymentResult.builder()
          .success(true)
          .paymentUrl(paymentUrl)
          .transactionId(String.valueOf(request.getBookingId()))
          .status(PaymentStatus.PENDING)
          .build();

    } catch (Exception e) {
      log.error(
          "Error creating VNPay payment for booking {}: {}",
          request.getBookingId(),
          e.getMessage());

      return PaymentResult.builder()
          .success(false)
          .errorMessage("Không thể tạo liên kết thanh toán")
          .status(PaymentStatus.FAILED)
          .build();
    }
  }

  @Override
  public PaymentVerificationResult verifyPayment(PaymentVerificationRequest request) {
    try {
      // Remove hash fields for verification
      Map<String, String> verifyParams = new HashMap<>(request.getParameters());
      verifyParams.remove("vnp_SecureHashType");
      verifyParams.remove("vnp_SecureHash");

      String calculatedHash = VNPayConfig.hashAllFields(verifyParams);
      boolean isValidSignature = calculatedHash.equals(request.getSecureHash());

      if (!isValidSignature) {
        return PaymentVerificationResult.builder()
            .valid(false)
            .status(PaymentStatus.FAILED)
            .errorMessage("Chữ ký không hợp lệ")
            .build();
      }

      PaymentStatus status = mapTransactionStatus(request.getTransactionStatus());

      return PaymentVerificationResult.builder()
          .valid(true)
          .status(status)
          .transactionId(request.getTransactionId())
          .build();

    } catch (Exception e) {
      log.error("Error verifying VNPay payment: {}", e.getMessage());

      return PaymentVerificationResult.builder()
          .valid(false)
          .status(PaymentStatus.FAILED)
          .errorMessage("Lỗi xác thực thanh toán")
          .build();
    }
  }

  @Override
  public String getPaymentMethod() {
    return PAYMENT_METHOD;
  }

  private String buildPaymentUrl(PaymentRequest request) {
    Map<String, String> params = buildPaymentParams(request);
    String queryString = buildQueryString(params);
    String secureHash = createSecureHash(params);

    return VNPayConfig.vnpPayUrl + "?" + queryString + "&vnp_SecureHash=" + secureHash;
  }

  private Map<String, String> buildPaymentParams(PaymentRequest request) {
    Map<String, String> params = new HashMap<>();

    params.put("vnp_Version", VERSION);
    params.put("vnp_Command", COMMAND);
    params.put("vnp_TmnCode", VNPayConfig.vnpTmnCode);
    params.put("vnp_Amount", formatAmount(request.getAmount()));
    params.put("vnp_CurrCode", CURRENCY_CODE);
    params.put("vnp_TxnRef", String.valueOf(request.getBookingId()));
    params.put("vnp_OrderInfo", request.getOrderInfo());
    params.put("vnp_OrderType", ORDER_TYPE);
    params.put("vnp_Locale", request.getLocale());
    params.put("vnp_ReturnUrl", request.getReturnUrl());

    // Add timestamps
    addTimestamps(params);

    return params;
  }

  private String formatAmount(BigDecimal amount) {
    return String.valueOf(amount.multiply(new BigDecimal(100)).longValue());
  }

  private void addTimestamps(Map<String, String> params) {
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

    String createDate = formatter.format(calendar.getTime());
    params.put("vnp_CreateDate", createDate);

    calendar.add(Calendar.MINUTE, PAYMENT_TIMEOUT_MINUTES);
    String expireDate = formatter.format(calendar.getTime());
    params.put("vnp_ExpireDate", expireDate);
  }

  private String buildQueryString(Map<String, String> params) {
    List<String> fieldNames = new ArrayList<>(params.keySet());
    Collections.sort(fieldNames);

    StringBuilder query = new StringBuilder();
    for (int i = 0; i < fieldNames.size(); i++) {
      String fieldName = fieldNames.get(i);
      String fieldValue = params.get(fieldName);

      if (fieldValue != null && !fieldValue.isEmpty()) {
        query.append(fieldName).append('=').append(fieldValue);
        if (i < fieldNames.size() - 1) {
          query.append('&');
        }
      }
    }

    return query.toString();
  }

  private String createSecureHash(Map<String, String> params) {
    List<String> fieldNames = new ArrayList<>(params.keySet());
    Collections.sort(fieldNames);

    StringBuilder hashData = new StringBuilder();
    for (int i = 0; i < fieldNames.size(); i++) {
      String fieldName = fieldNames.get(i);
      String fieldValue = params.get(fieldName);

      if (fieldValue != null && !fieldValue.isEmpty()) {
        hashData.append(fieldName).append('=').append(fieldValue);
        if (i < fieldNames.size() - 1) {
          hashData.append('&');
        }
      }
    }

    return VNPayConfig.hmacSHA512(VNPayConfig.vnpHashSecret, hashData.toString());
  }

  private PaymentStatus mapTransactionStatus(String vnpayStatus) {
    return switch (vnpayStatus) {
      case "00" -> PaymentStatus.SUCCESS;
      case "24" -> PaymentStatus.CANCELLED;
      case "02" -> PaymentStatus.EXPIRED;
      default -> PaymentStatus.FAILED;
    };
  }
}
