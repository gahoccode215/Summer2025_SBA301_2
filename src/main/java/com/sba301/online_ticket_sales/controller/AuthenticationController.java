package com.sba301.online_ticket_sales.controller;

import com.sba301.online_ticket_sales.dto.auth.request.AuthenticationResponse;
import com.sba301.online_ticket_sales.dto.auth.request.ChangePasswordRequest;
import com.sba301.online_ticket_sales.dto.auth.request.LoginRequest;
import com.sba301.online_ticket_sales.dto.auth.request.RegisterRequest;
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

  @PostMapping("/outbound/authentication")
  ResponseEntity<ApiResponseDTO<AuthenticationResponse>> outboundAuthenticate(
      @RequestParam("code") String code) {
    return ResponseEntity.ok(
        ApiResponseDTO.<AuthenticationResponse>builder()
            .code(HttpStatus.OK.value())
            .result(authenticationService.outboundAuthenticate(code))
            .message("Login Google thành công")
            .build());
  }
}
