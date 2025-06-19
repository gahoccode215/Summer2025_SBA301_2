package com.sba301.online_ticket_sales.config;

import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VNPayConfig {
  @Value("${vnpay.url}")
  public static String vnpPayUrl;

  @Value("${vnpay.return-url}")
  public static String vnpReturnUrl;

  @Value("${vnpay.tmn-code}")
  public static String vnpTmnCode;

  @Value("${vnpay.hash-secret}")
  public static String vnpHashSecret;

  public static String hmacSHA512(final String key, final String data) {
    try {
      final Mac hmac512 = Mac.getInstance("HmacSHA512");
      byte[] hmacKeyBytes = key.getBytes();
      final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
      hmac512.init(secretKey);
      byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
      byte[] result = hmac512.doFinal(dataBytes);

      StringBuilder sb = new StringBuilder(2 * result.length);
      for (byte b : result) {
        sb.append(String.format("%02x", b & 0xff));
      }
      return sb.toString();
    } catch (Exception ex) {
      return "";
    }
  }

  public static String hashAllFields(Map<String, String> fields) {
    List<String> fieldNames = new ArrayList<>(fields.keySet());
    Collections.sort(fieldNames);

    StringBuilder hashData = new StringBuilder();
    Iterator<String> itr = fieldNames.iterator();

    while (itr.hasNext()) {
      String fieldName = itr.next();
      String fieldValue = fields.get(fieldName);
      if (fieldValue != null && fieldValue.length() > 0) {
        hashData.append(fieldName).append("=").append(fieldValue);
        if (itr.hasNext()) {
          hashData.append("&");
        }
      }
    }

    return hmacSHA512(vnpHashSecret, hashData.toString());
  }
}
