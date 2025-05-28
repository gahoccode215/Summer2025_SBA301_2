package com.sba301.online_ticket_sales.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    // AUTHENTICATION EXCEPTION (1000 - 1099)
    UNAUTHENTICATED(1000, "Vui lòng đăng nhập để sử dụng chức năng này", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1001, "Không thể thực hiện chức năng này", HttpStatus.FORBIDDEN),
    EMAIL_EXSITED(1002, "Email đã tồn tại", HttpStatus.BAD_REQUEST),
    EMAIL_OR_PASSWORD_NOT_CORRECT(1003, "Email hoặc mật khẩu không chính xác", HttpStatus.BAD_REQUEST),
    ACCOUNT_HAS_BEEN_DISABLE(1004, "Tài khoản của bạn chưa kích hoạt hoặc bị khóa", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(1005, "Invalid token", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1006, "Role not found", HttpStatus.BAD_REQUEST)
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
