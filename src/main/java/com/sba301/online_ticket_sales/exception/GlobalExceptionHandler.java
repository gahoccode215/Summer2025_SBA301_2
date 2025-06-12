package com.sba301.online_ticket_sales.exception;

import com.sba301.online_ticket_sales.dto.common.ApiResponseDTO;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /** Xử lý lỗi validation từ @Valid annotation Ví dụ: @Past, @NotBlank, @NotNull, etc. */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponseDTO<Map<String, String>>> handleValidationException(
      MethodArgumentNotValidException exception) {
    log.warn("Validation failed: {}", exception.getMessage());

    // Tạo map chứa field errors
    Map<String, String> fieldErrors = new HashMap<>();
    exception
        .getBindingResult()
        .getFieldErrors()
        .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

    // Tạo message tổng hợp
    String message =
        "Validation failed: "
            + fieldErrors.entrySet().stream()
                .map(entry -> entry.getKey() + " - " + entry.getValue())
                .collect(Collectors.joining(", "));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ApiResponseDTO.<Map<String, String>>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .result(fieldErrors) // Trả về chi tiết lỗi từng field
                .build());
  }

  /** Xử lý lỗi type mismatch (ví dụ: gửi string cho field integer) */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponseDTO<Void>> handleTypeMismatchException(
      MethodArgumentTypeMismatchException exception) {
    log.warn("Type mismatch: {}", exception.getMessage());

    String message =
        String.format(
            "Invalid value '%s' for parameter '%s'. Expected type: %s",
            exception.getValue(),
            exception.getName(),
            exception.getRequiredType() != null
                ? exception.getRequiredType().getSimpleName()
                : "unknown");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ApiResponseDTO.<Void>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .build());
  }

  /** Xử lý lỗi nhập data khong hop le */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiResponseDTO<Void>> handleDataIntegrityViolationException(
      DataIntegrityViolationException exception) {
    log.warn("Data violation: {}", exception.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ApiResponseDTO.<Void>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("Data input invalid")
                .build());
  }

  /** Xử lý lỗi không có quyền truy cập (401 Unauthorized). */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponseDTO<Void>> handleAccessDeniedException(
      AccessDeniedException exception) {
    log.warn("Access Denied: {}", exception.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(
            ApiResponseDTO.<Void>builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(ErrorCode.UNAUTHENTICATED.getMessage())
                .build());
  }

  /** Xử lý lỗi có định nghĩa trong hệ thống (AppException). */
  @ExceptionHandler(AppException.class)
  public ResponseEntity<ApiResponseDTO<Void>> handleAppException(AppException exception) {
    log.error("Application error: {}", exception.getMessage());
    return ResponseEntity.status(exception.getErrorCode().getStatusCode())
        .body(
            ApiResponseDTO.<Void>builder()
                .code(exception.getErrorCode().getCode())
                .message(exception.getMessage())
                .build());
  }

  /** Xử lý tất cả các lỗi chưa được bắt trước đó (Exception.class). */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponseDTO<Void>> handleUncaughtException(Exception exception) {
    log.error("Uncaught exception: ", exception);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            ApiResponseDTO.<Void>builder()
                .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
                .build());
  }
}
