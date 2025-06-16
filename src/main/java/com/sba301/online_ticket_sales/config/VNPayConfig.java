package com.sba301.online_ticket_sales.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "vnpay")
public class VNPayConfig {
  private String tmnCode;
  private String hashSecret;
  private String url;
  private String apiUrl;
  private String returnUrl;
  private String version;
  private String command;
  private String orderType;
  private String currencyCode;
}
