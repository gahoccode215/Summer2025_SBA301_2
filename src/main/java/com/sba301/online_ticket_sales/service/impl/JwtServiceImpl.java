package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.enums.TokenType;
import com.sba301.online_ticket_sales.service.JwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiryHour}")
    private long expiryHour;

    @Value("${jwt.expiryDay}")
    private long expiryDay;

    @Value("${jwt.accessKey}")
    private String accessKey;

    @Value("${jwt.refreshKey}")
    private String refreshKey;

    @Value("${jwt.resetKey}")
    private String resetKey;

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
