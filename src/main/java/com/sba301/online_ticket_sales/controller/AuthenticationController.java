package com.sba301.online_ticket_sales.controller;

import com.sba301.online_ticket_sales.dto.auth.request.*;
import com.sba301.online_ticket_sales.dto.auth.response.TokenResponse;
import com.sba301.online_ticket_sales.dto.common.ApiResponseDTO;
import com.sba301.online_ticket_sales.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Authentication Controller")
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
  AuthenticationService authenticationService;

  @PostMapping("/register")
  public ResponseEntity<ApiResponseDTO<Void>> register(
      @RequestBody @Valid RegisterRequest request) {
    authenticationService.register(request);
    return ResponseEntity.ok(
        ApiResponseDTO.<Void>builder()
            .code(HttpStatus.OK.value())
            .message("Đăng ký thành công")
            .build());
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponseDTO<TokenResponse>> accessToken(
      @RequestBody LoginRequest request) {
    return ResponseEntity.ok(
        ApiResponseDTO.<TokenResponse>builder()
            .code(HttpStatus.OK.value())
            .message("Đăng nhập thành công")
            .result(authenticationService.login(request))
            .build());
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponseDTO<Void>> removeToken(HttpServletRequest request) {
    authenticationService.logout(request);
    return ResponseEntity.ok(
        ApiResponseDTO.<Void>builder()
            .code(HttpStatus.OK.value())
            .message("Đăng xuất thành công")
            .build());
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<ApiResponseDTO<TokenResponse>> refreshToken(HttpServletRequest request) {
    return ResponseEntity.ok(
        ApiResponseDTO.<TokenResponse>builder()
            .code(HttpStatus.OK.value())
            .result(authenticationService.refreshToken(request))
            .build());
  }

  @Operation(
      summary = "Đổi mật khẩu",
      description =
          "Đổi mật khẩu của người dùng hiện tại. Yêu cầu mật khẩu hiện tại, mật khẩu mới và xác nhận mật khẩu mới. Yêu cầu người dùng đã đăng nhập.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Đổi mật khẩu thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description =
            "Dữ liệu đầu vào không hợp lệ, mật khẩu hiện tại không đúng hoặc mật khẩu mới không khớp",
        content = @Content),
    @ApiResponse(
        responseCode = "401",
        description = "Chưa đăng nhập hoặc token không hợp lệ",
        content = @Content)
  })
  @PutMapping("/change-password")
  // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CUSTOMER')")
  public ResponseEntity<ApiResponseDTO<Void>> changePassword(
      @Valid @RequestBody ChangePasswordRequest request) {
    authenticationService.changePassword(request);
    return ResponseEntity.ok(
        ApiResponseDTO.<Void>builder()
            .code(HttpStatus.OK.value())
            .message("Đổi mật khẩu thành công")
            .build());
  }


  @Operation(
          summary = "Quên mật khẩu",
          description = "Gửi mã OTP đến email người dùng để xác nhận đặt lại mật khẩu. Không yêu cầu đăng nhập.")
  @ApiResponses({
          @ApiResponse(
                  responseCode = "200",
                  description = "Gửi OTP thành công",
                  content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
          @ApiResponse(
                  responseCode = "400",
                  description = "Email không hợp lệ hoặc không tồn tại trong hệ thống",
                  content = @Content)
  })
  @PostMapping("/forgot-password")
  public ResponseEntity<ApiResponseDTO<Void>> forgotPassword(
          @Valid @RequestBody ForgotPasswordRequest request) {
    authenticationService.sendForgotPasswordOtp(request);
    return ResponseEntity.ok(
            ApiResponseDTO.<Void>builder()
                    .code(HttpStatus.OK.value())
                    .message("Gửi OTP thành công")
                    .build());
  }


  @Operation(
          summary = "Xác nhận mã OTP",
          description = "Xác nhận mã OTP đã gửi qua email trong quá trình quên mật khẩu. Không yêu cầu đăng nhập.")
  @ApiResponses({
          @ApiResponse(
                  responseCode = "200",
                  description = "Xác nhận OTP thành công",
                  content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
          @ApiResponse(
                  responseCode = "400",
                  description = "OTP không đúng hoặc đã hết hạn",
                  content = @Content)
  })
  @PostMapping("/confirm-otp")
  public ResponseEntity<ApiResponseDTO<String>> confirmOtp(
          @Valid @RequestBody ConfirmOTPRequest request) {
    return ResponseEntity.ok(
            ApiResponseDTO.<String>builder()
                    .code(HttpStatus.OK.value())
                    .message("Xác nhận OTP thành công")
                    .result(authenticationService.confirmOTP(request))
                    .build());
  }

  @Operation(
          summary = "Đặt lại mật khẩu",
          description = "Đặt lại mật khẩu mới sau khi đã xác nhận OTP. Không yêu cầu đăng nhập.")
  @ApiResponses({
          @ApiResponse(
                  responseCode = "200",
                  description = "Đặt lại mật khẩu thành công",
                  content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
          @ApiResponse(
                  responseCode = "400",
                  description = "OTP chưa được xác thực hoặc mật khẩu không hợp lệ",
                  content = @Content)
  })
  @PostMapping("/reset-password")
  public ResponseEntity<ApiResponseDTO<TokenResponse>> resetPassword(
          @Valid @RequestBody ResetPasswordRequest request) {
    return ResponseEntity.ok(
            ApiResponseDTO.<TokenResponse>builder()
                    .code(HttpStatus.OK.value())
                    .message("Đặt lại mật khẩu thành công")
                    .result(authenticationService.resetPassword(request))
                    .build());
  }


  @Operation(
          summary = "Gửi lại mã OTP",
          description = "Gửi lại mã OTP đến email người dùng trong quá trình quên mật khẩu. Không yêu cầu đăng nhập.")
  @ApiResponses({
          @ApiResponse(
                  responseCode = "200",
                  description = "Gửi lại OTP thành công",
                  content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
          @ApiResponse(
                  responseCode = "400",
                  description = "Email không hợp lệ hoặc không tồn tại",
                  content = @Content)
  })
  @PostMapping("/resend-otp")
  public ResponseEntity<ApiResponseDTO<Void>> resendOtp(
          @Valid @RequestBody ResendOTPRequest request) {
    authenticationService.resendOTP(request);
    return ResponseEntity.ok(
            ApiResponseDTO.<Void>builder()
                    .code(HttpStatus.OK.value())
                    .message("Gửi lại OTP thành công")
                    .build());
  }



}
