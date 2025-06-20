package com.sba301.online_ticket_sales.dto.auth.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {
  private String accessToken;

  private String refreshToken;

  private Long userId;
}
