package com.sba301.online_ticket_sales.controller;

import com.sba301.online_ticket_sales.dto.auth.request.LoginRequest;
import com.sba301.online_ticket_sales.dto.auth.response.TokenResponse;
import com.sba301.online_ticket_sales.dto.common.ApiResponse;
import com.sba301.online_ticket_sales.dto.auth.request.RegisterRequest;
import com.sba301.online_ticket_sales.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Authentication Controller")
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody @Valid RegisterRequest request) {
        authenticationService.register(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Đăng ký thành công")
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> accessToken(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.<TokenResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Đăng nhập thành công")
                .result(authenticationService.login(request))
                .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> removeToken(HttpServletRequest request) {
        authenticationService.logout(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Đăng xuất thành công")
                .build());
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.<TokenResponse>builder()
                .code(HttpStatus.OK.value())
                .result(authenticationService.refreshToken(request))
                .build());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody String email) {
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .result(authenticationService.forgotPassword(email))
                .build());
    }
}
