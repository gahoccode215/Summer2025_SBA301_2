package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.auth.request.*;
import com.sba301.online_ticket_sales.dto.auth.response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
  void register(RegisterRequest request);

  TokenResponse login(LoginRequest request);

  void logout(HttpServletRequest request);

  TokenResponse refreshToken(HttpServletRequest request);

  void changePassword(ChangePasswordRequest request);

  void sendForgotPasswordOtp(ForgotPasswordRequest request);

  String confirmOTP(ConfirmOTPRequest request);

    void resendOTP(ResendOTPRequest request);

  TokenResponse resetPassword(ResetPasswordRequest request);
}
