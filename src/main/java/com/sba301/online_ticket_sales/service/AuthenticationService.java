package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.auth.request.LoginRequest;
import com.sba301.online_ticket_sales.dto.auth.request.RegisterRequest;
import com.sba301.online_ticket_sales.dto.auth.response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    void register(RegisterRequest request);
    TokenResponse login(LoginRequest request);
    void logout(HttpServletRequest request);
}
