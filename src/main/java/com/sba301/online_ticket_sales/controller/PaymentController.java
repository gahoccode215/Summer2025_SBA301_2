package com.sba301.online_ticket_sales.controller;

import com.sba301.online_ticket_sales.dto.booking.request.VnPayCallbackParamRequest;
import com.sba301.online_ticket_sales.dto.common.ApiResponseDTO;
import com.sba301.online_ticket_sales.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Payment Controller", description = "APIs Payments")
@RequestMapping("/api/v1/payments")
public class PaymentController {
  PaymentService paymentService;

  @Operation(summary = "Get payment link", description = "Get payment link for a booking")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Successfully created payment link",
        content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input data or missing required fields",
        content = @Content)
  })
  @GetMapping("/{orderCode}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponseDTO<String>> getPaymentLink(
      @PathVariable("orderCode") String orderCode, HttpServletRequest request) {
    log.info("Received request to create payment link for order code: {}", orderCode);
    String response = paymentService.createPayment(orderCode, request);
    return ResponseEntity.ok(
        ApiResponseDTO.<String>builder()
            .code(HttpStatus.OK.value())
            .message("Payment link created successfully")
            .result(response)
            .build());
  }

  @Operation(
      summary = "Handle VNPay callback",
      description = "Handle VNPay callback for payment status")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successfully handled VNPay callback",
        content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input data or missing required fields",
        content = @Content)
  })
  @GetMapping("/callback")
  public ResponseEntity<ApiResponseDTO<Void>> handleVNPayCallback(
      VnPayCallbackParamRequest request) {
    log.info("Received VNPay callback request");
    paymentService.handlePaymentCallback(request);
    return ResponseEntity.ok(
        ApiResponseDTO.<Void>builder()
            .code(HttpStatus.OK.value())
            .message("VNPay callback handled successfully")
            .build());
  }
}
