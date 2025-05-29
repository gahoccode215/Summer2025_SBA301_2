package com.sba301.online_ticket_sales.exception;

import com.sba301.online_ticket_sales.dto.common.ApiResponseDTO;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import java.nio.file.AccessDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

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
