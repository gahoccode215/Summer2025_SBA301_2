package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.enums.TokenType;
import com.sba301.online_ticket_sales.service.JwtService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtServiceImpl implements JwtService {
    @Override
    public String generateToken(UserDetails user) {
        return "";
    }

    @Override
    public String generateRefreshToken(UserDetails user) {
        return "";
    }

    @Override
    public String generateResetToken(UserDetails user) {
        return "";
    }

    @Override
    public String extractUsername(String token, TokenType type) {
        return "";
    }

    @Override
    public boolean isValid(String token, TokenType type, UserDetails user) {
        return false;
    }
}
