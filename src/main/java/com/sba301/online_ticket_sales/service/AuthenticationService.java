package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.request.LoginRequest;
import com.sba301.online_ticket_sales.dto.request.RegisterRequest;
import com.sba301.online_ticket_sales.dto.response.LoginResponse;

public interface AuthenticationService {
    void register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
}
