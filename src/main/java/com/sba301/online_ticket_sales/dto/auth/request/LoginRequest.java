package com.sba301.online_ticket_sales.dto.auth.request;

import lombok.*;

@Getter
public class LoginRequest {
  private String email;
  private String password;
}
