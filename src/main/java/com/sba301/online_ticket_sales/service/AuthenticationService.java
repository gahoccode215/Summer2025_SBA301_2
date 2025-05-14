package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.request.RegisterRequest;

public interface AuthenticationService {
    void register(RegisterRequest request);
}
