package com.sba301.online_ticket_sales.config;

import static com.sba301.online_ticket_sales.enums.TokenType.ACCESS_TOKEN;

import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.model.RedisToken;
import com.sba301.online_ticket_sales.repository.UserRepository;
import com.sba301.online_ticket_sales.service.JwtService;
import com.sba301.online_ticket_sales.service.RedisTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
@RequiredArgsConstructor
public class PreFilter extends OncePerRequestFilter {

  private static final String BEARER_PREFIX = "Bearer ";
  private static final String UNAUTHORIZED_MESSAGE = "Token is invalid or has been logged out";

  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final RedisTokenService redisTokenService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws IOException, ServletException {
    log.debug("Processing request in PreFilter");

    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (!isValidAuthHeader(authHeader)) {
      log.debug("No valid Bearer token found in Authorization header");
      filterChain.doFilter(request, response);
      return;
    }

    String token = extractToken(authHeader);
    String email = extractEmailFromToken(token);

    if (!isTokenValidInRedis(email, token, response)) {
      return;
    }

    authenticateUser(email, token, request);
    filterChain.doFilter(request, response);
  }

  /** Kiểm tra xem Authorization header có hợp lệ và chứa Bearer token hay không. */
  private boolean isValidAuthHeader(String authHeader) {
    return StringUtils.isNotBlank(authHeader) && authHeader.startsWith(BEARER_PREFIX);
  }

  /** Trích xuất token từ Authorization header. */
  private String extractToken(String authHeader) {
    return authHeader.substring(BEARER_PREFIX.length());
  }

  /** Trích xuất email từ token, ném ngoại lệ nếu token không hợp lệ. */
  private String extractEmailFromToken(String token) {
    try {
      String email = jwtService.extractEmail(token, ACCESS_TOKEN);
      if (StringUtils.isBlank(email)) {
        throw new AppException(ErrorCode.UNAUTHENTICATED);
      }
      return email;
    } catch (Exception e) {
      log.warn("Failed to extract email from token: {}", e.getMessage());
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }
  }

  /**
   * Kiểm tra xem token có tồn tại và hợp lệ trong Redis hay không. Nếu không hợp lệ, trả về
   * response 401.
   */
  private boolean isTokenValidInRedis(String email, String token, HttpServletResponse response)
      throws IOException {
    try {
      RedisToken redisToken = redisTokenService.getById(email);
      if (redisToken == null || !redisToken.getAccessToken().equals(token)) {
        log.warn("Token for email {} is invalid or has been logged out", email);
        sendUnauthorizedResponse(response);
        return false;
      }
      return true;
    } catch (AppException e) {
      log.warn("Token not found in Redis for email {}: {}", email, e.getMessage());
      sendUnauthorizedResponse(response);
      return false;
    }
  }

  /** Gửi response 401 khi token không hợp lệ. */
  private void sendUnauthorizedResponse(HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.getWriter().write(UNAUTHORIZED_MESSAGE);
  }

  /** Xác thực người dùng và thiết lập SecurityContext nếu chưa được xác thực. */
  private void authenticateUser(String identifier, String token, HttpServletRequest request) {
    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      log.debug("User already authenticated for identifier: {}", identifier);
      return;
    }
    try {
      // Tìm user theo username hoặc email
      UserDetails userDetails =
          userRepository
              .findByUsernameOrEmail(identifier)
              .orElseThrow(() -> new UsernameNotFoundException("User not found: " + identifier));

      if (jwtService.isValid(token, ACCESS_TOKEN, userDetails)) {
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        log.debug("Successfully authenticated user: {}", identifier);
      } else {
        log.warn("JWT token is invalid for identifier: {}", identifier);
        throw new AppException(ErrorCode.UNAUTHENTICATED);
      }
    } catch (UsernameNotFoundException e) {
      log.warn("User not found for identifier: {}", identifier);
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }
  }
}
