package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.auth.request.LoginRequest;
import com.sba301.online_ticket_sales.dto.auth.request.RegisterRequest;
import com.sba301.online_ticket_sales.dto.auth.response.TokenResponse;

public interface AuthenticationService {
    void register(RegisterRequest request);
    TokenResponse login(LoginRequest request);
}
