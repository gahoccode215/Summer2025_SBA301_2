package com.sba301.online_ticket_sales.dto.auth.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private String tokenType;
}
