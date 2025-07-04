package com.sba301.online_ticket_sales.service.impl;

import static com.sba301.online_ticket_sales.enums.TokenType.*;

import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.enums.TokenType;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
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
    return generateToken(new HashMap<>(), user);
  }

  @Override
  public String generateRefreshToken(UserDetails user) {
    return generateRefreshToken(new HashMap<>(), user);
  }

  //    @Override
  //    public String generateResetToken(UserDetails user) {
  //        return generateResetToken(new HashMap<>(), user);
  //    }

  @Override
  public String extractEmail(String token, TokenType type) {
    log.info("TOKEN EMAIL {}", token);
    log.info("extract email  {}", extractClaim(token, type, Claims::getSubject));
    return extractClaim(token, type, Claims::getSubject);
  }

  @Override
  public boolean isValid(String token, TokenType type, UserDetails user) {
    log.info("---------- isValid ----------");
    final String email = extractEmail(token, type);
    return (email.equals(user.getUsername()) && !isTokenExpired(token, type));
  }

  private String generateToken(Map<String, Object> claims, UserDetails userDetails) {
    log.info("---------- generateToken ----------");
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * expiryHour))
        .signWith(getKey(ACCESS_TOKEN), SignatureAlgorithm.HS256)
        .compact();
  }

  private String generateRefreshToken(Map<String, Object> claims, UserDetails userDetails) {
    log.info("---------- generateRefreshToken ----------");
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * expiryDay))
        .signWith(getKey(REFRESH_TOKEN), SignatureAlgorithm.HS256)
        .compact();
  }

  private String generateResetToken(Map<String, Object> claims, UserDetails userDetails) {
    log.info("---------- generateResetToken ----------");
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
        .signWith(getKey(RESET_TOKEN), SignatureAlgorithm.HS256)
        .compact();
  }

  private Key getKey(TokenType type) {
    log.info("---------- getKey ----------");
    switch (type) {
      case ACCESS_TOKEN -> {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
      }
      case REFRESH_TOKEN -> {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
      }
      case RESET_TOKEN -> {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(resetKey));
      }
      default -> throw new AppException(ErrorCode.INVALID_TOKEN);
    }
  }

  private <T> T extractClaim(String token, TokenType type, Function<Claims, T> claimResolver) {
    final Claims claims = extraAllClaim(token, type);
    return claimResolver.apply(claims);
  }

  private Claims extraAllClaim(String token, TokenType type) {
    return Jwts.parserBuilder().setSigningKey(getKey(type)).build().parseClaimsJws(token).getBody();
  }

  private boolean isTokenExpired(String token, TokenType type) {
    return extractExpiration(token, type).before(new Date());
  }

  private Date extractExpiration(String token, TokenType type) {
    return extractClaim(token, type, Claims::getExpiration);
  }
}
