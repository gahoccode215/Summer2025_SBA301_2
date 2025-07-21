package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.enums.TokenType;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

  String extractSubject(String token, TokenType tokenType);

  String generateToken(UserDetails user);

  String generateRefreshToken(UserDetails user);

  String extractEmail(String token, TokenType type);

  boolean isValid(String token, TokenType type, UserDetails user);
}
