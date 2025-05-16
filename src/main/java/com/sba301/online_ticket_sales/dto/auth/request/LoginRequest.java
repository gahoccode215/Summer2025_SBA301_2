package com.sba301.online_ticket_sales.dto.auth.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
public class LoginRequest {
    private String email;
    private String password;
}
