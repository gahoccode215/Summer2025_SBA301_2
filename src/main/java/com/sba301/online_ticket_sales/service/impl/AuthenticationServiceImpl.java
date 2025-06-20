package com.sba301.online_ticket_sales.service.impl;

import static com.sba301.online_ticket_sales.enums.TokenType.ACCESS_TOKEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.sba301.online_ticket_sales.constant.PredefinedRole;
import com.sba301.online_ticket_sales.dto.auth.request.*;
import com.sba301.online_ticket_sales.dto.auth.response.TokenResponse;
import com.sba301.online_ticket_sales.dto.common.OTPMailDTO;
import com.sba301.online_ticket_sales.entity.Role;
import com.sba301.online_ticket_sales.entity.User;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.enums.OTPType;
import com.sba301.online_ticket_sales.enums.TokenType;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.mapper.AuthenticationMapper;
import com.sba301.online_ticket_sales.model.RedisToken;
import com.sba301.online_ticket_sales.repository.RoleRepository;
import com.sba301.online_ticket_sales.repository.UserRepository;
import com.sba301.online_ticket_sales.repository.httpclient.OutboundIdentityClient;
import com.sba301.online_ticket_sales.repository.httpclient.OutboundUserClient;
import com.sba301.online_ticket_sales.service.*;
import com.sba301.online_ticket_sales.service.AuthenticationService;
import com.sba301.online_ticket_sales.service.JwtService;
import com.sba301.online_ticket_sales.service.RedisTokenService;
import com.sba301.online_ticket_sales.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {

  UserRepository userRepository;
  AuthenticationMapper authenticationMapper;
  UserService userService;
  AuthenticationManager authenticationManager;
  JwtService jwtService;
  RedisTokenService redisTokenService;
  RedisSecretService redisSecretService;
  PasswordEncoder passwordEncoder;
  OutboundIdentityClient outboundIdentityClient;
  OutboundUserClient outboundUserClient;
  RoleRepository roleRepository;

  @NonFinal
  @Value("${outbound.identity.client-id}")
  protected String CLIENT_ID;

  @NonFinal
  @Value("${outbound.identity.client-secret}")
  protected String CLIENT_SECRET;

  @NonFinal
  @Value("${outbound.identity.redirect-uri}")
  protected String REDIRECT_URI;

  @NonFinal protected final String GRANT_TYPE = "authorization_code";
  UserMailQueueProducer userMailQueueProducer;

  private final String OTP_KEY = "OTP_KEY_";
  private final String RESET_PASSWORD_KEY = "RESET_PASSWORD_KEY_";

  @Override
  public void register(RegisterRequest request) {
    userRepository.save(authenticationMapper.toUser(request));
  }

  @Override
  public TokenResponse login(LoginRequest request) {
    User user = userService.getByEmail(request.getEmail());
    if (!user.isEnabled()) {
      throw new AppException(ErrorCode.ACCOUNT_HAS_BEEN_DISABLE);
    }
    List<String> roles = userService.getAllRolesByUserId(user.getId());
    List<SimpleGrantedAuthority> authorities =
        roles.stream().map(SimpleGrantedAuthority::new).toList();
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              request.getEmail(), request.getPassword(), authorities));
    } catch (BadCredentialsException e) {
      throw new AppException(ErrorCode.EMAIL_OR_PASSWORD_NOT_CORRECT);
    }

    // create new access token
    String accessToken = jwtService.generateToken(user);

    // create new refresh token
    String refreshToken = jwtService.generateRefreshToken(user);
    log.info(
        "REDIS TOKEN {}",
        RedisToken.builder()
            .id(user.getUsername())
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build());
    redisTokenService.save(
        RedisToken.builder()
            .id(user.getUsername())
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build());

    return TokenResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .userId(user.getId())
        .build();
  }

  @Override
  public void logout(HttpServletRequest request) {
    log.info("VAO HAM LOGOUT - {}", request.getHeader(AUTHORIZATION));
    log.info("TOKEN -- {}", request.getHeader(AUTHORIZATION));
    final String authHeader = request.getHeader(AUTHORIZATION);
    if (StringUtils.isBlank(authHeader) || !authHeader.startsWith("Bearer ")) {
      throw new AppException(ErrorCode.INVALID_TOKEN);
    }
    final String token = authHeader.substring(7);
    final String email = jwtService.extractEmail(token, ACCESS_TOKEN);
    log.info("EMAIL {}", email);
    redisTokenService.remove(email);
  }

  @Override
  /**
   * Refresh token
   *
   * @param request
   * @return
   */
  public TokenResponse refreshToken(HttpServletRequest request) {
    log.info("---------- refreshToken ----------");

    final String refreshToken = request.getHeader("X-Refresh-Token");
    if (StringUtils.isBlank(refreshToken)) {
      throw new AppException(ErrorCode.INVALID_TOKEN);
    }

    log.info("Refresh token received: {}", refreshToken);

    final String userName = jwtService.extractEmail(refreshToken, TokenType.REFRESH_TOKEN);
    if (StringUtils.isBlank(userName)) {
      throw new AppException(ErrorCode.INVALID_TOKEN);
    }
    var user = userService.getByEmail(userName);
    if (!jwtService.isValid(refreshToken, TokenType.REFRESH_TOKEN, user)) {
      throw new AppException(ErrorCode.INVALID_TOKEN);
    }

    // Xóa access token cũ trong Redis
    redisTokenService.remove(user.getUsername());

    // Tạo access token mới
    String accessToken = jwtService.generateToken(user);

    // Lưu token mới vào Redis
    redisTokenService.save(
        RedisToken.builder()
            .id(user.getUsername())
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build());

    return TokenResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .userId(user.getId())
        .build();
  }

  @Override
  public void changePassword(ChangePasswordRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication.getPrincipal() instanceof String) {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }
    User user = (User) authentication.getPrincipal();

    // Verify current password
    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
      throw new AppException(ErrorCode.INCORRECT_PASSWORD);
    }

    // Check if new password matches confirm password
    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
      throw new AppException(ErrorCode.PASSWORD_MISMATCH);
    }
    // Update password
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);
  }

  @Override
  public TokenResponse outboundAuthenticate(String code) {
    var response =
        outboundIdentityClient.exchangeToken(
            ExchangeTokenRequest.builder()
                .code(code)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .redirectUri(REDIRECT_URI)
                .grantType(GRANT_TYPE)
                .build());

    log.info("TOKEN RESPONSE {}", response);
    // Get user info
    var userInfo = outboundUserClient.getUserInfo("json", response.getAccessToken());

    log.info("User Info {}", userInfo);

    Role customerRole =
        roleRepository
            .findByName(PredefinedRole.CUSTOMER_ROLE)
            .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

    // Onboard user
    var user =
        userRepository
            .findByEmail(userInfo.getEmail())
            .orElseGet(
                () ->
                    userRepository.save(
                        User.builder()
                            .email(userInfo.getEmail())
                            .fullName(userInfo.getName())
                            .password("")
                            .roles(Collections.singletonList(customerRole))
                            .build()));
    // create new access token
    String accessToken = jwtService.generateToken(user);

    // create new refresh token
    String refreshToken = jwtService.generateRefreshToken(user);
    redisTokenService.save(
        RedisToken.builder()
            .id(user.getUsername())
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build());

    return TokenResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .userId(user.getId())
        .build();
  }

  public void sendForgotPasswordOtp(ForgotPasswordRequest request) {
    log.info("Forgot password OTP to email: {}", request.getEmail());
    User user = userService.getByEmail(request.getEmail());
    String otpCode = generateOtp();
    String otpKey = OTP_KEY + user.getId();
    redisSecretService.saveSecretKey(otpKey, otpCode);
    log.info("Generated OTP: {}", otpCode);
    userMailQueueProducer.sendMailMessage(
        OTPMailDTO.builder()
            .otpCode(otpCode)
            .receiverMail(request.getEmail())
            .type(OTPType.FORGOT_PASSWORD)
            .build());
  }

  @Override
  public String confirmOTP(ConfirmOTPRequest request) {
    log.info("Confirming OTP for email: {}", request.getEmail());
    User user = userService.getByEmail(request.getEmail());
    String otpKey = OTP_KEY + user.getId();
    if (!redisSecretService.isValidSecretKey(otpKey, request.getOtp())) {
      throw new AppException(ErrorCode.SECRET_KEY_INCORRECT);
    }
    redisSecretService.removeSecretKey(otpKey);
    log.info("OTP confirmed for email: {}", request.getEmail());
    String secretKey = Base64.getEncoder().encodeToString(request.getEmail().getBytes());

    String resetPasswordKey = RESET_PASSWORD_KEY + user.getId();
    redisSecretService.saveSecretKey(resetPasswordKey, secretKey);
    log.info("Reset password key saved for user ID: {}", user.getId());
    return secretKey;
  }

  @Override
  public void resendOTP(ResendOTPRequest request) {
    log.info("Resending OTP to email: {}", request.getEmail());
    User user = userService.getByEmail(request.getEmail());
    String otpKey = OTP_KEY + user.getId();
    boolean isExistOtp = redisSecretService.isOtpExists(otpKey);
    if (isExistOtp) redisSecretService.removeSecretKey(otpKey);
    String otpCode = generateOtp();
    redisSecretService.saveSecretKey(otpKey, otpCode);
    log.info("Generated new OTP: {}", otpCode);
    userMailQueueProducer.sendMailMessage(
        OTPMailDTO.builder()
            .otpCode(otpCode)
            .receiverMail(request.getEmail())
            .type(request.getOtpType())
            .build());
  }

  @Override
  @Transactional
  public TokenResponse resetPassword(ResetPasswordRequest request) {
    log.info("Resetting password for email: {}", request.getEmail());
    User user = userService.getByEmail(request.getEmail());
    String resetPasswordKey = RESET_PASSWORD_KEY + user.getId();
    if (!redisSecretService.isValidSecretKey(resetPasswordKey, request.getResetKey())) {
      throw new AppException(ErrorCode.SECRET_KEY_INCORRECT);
    }
    redisSecretService.removeSecretKey(resetPasswordKey);

    boolean isPasswordMismatch = !request.getNewPassword().equals(request.getConfirmPassword());
    if (isPasswordMismatch) throw new AppException(ErrorCode.PASSWORD_MISMATCH);

    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);
    log.info("Password reset successfully for email: {}", request.getEmail());

    // create new access token
    String accessToken = jwtService.generateToken(user);

    // create new refresh token
    String refreshToken = jwtService.generateRefreshToken(user);
    log.info(
        "REDIS TOKEN {}",
        RedisToken.builder()
            .id(user.getUsername())
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build());
    redisTokenService.save(
        RedisToken.builder()
            .id(user.getUsername())
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build());

    return TokenResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .userId(user.getId())
        .build();
  }

  private String generateOtp() {
    Random random = new Random();
    int otp = random.nextInt(999999);
    return String.format("%06d", otp);
  }
}
