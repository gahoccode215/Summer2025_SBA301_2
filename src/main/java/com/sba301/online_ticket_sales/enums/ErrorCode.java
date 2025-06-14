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
  EMAIL_ALREADY_EXISTS(1002, "Email đã tồn tại", HttpStatus.BAD_REQUEST),
  EMAIL_OR_PASSWORD_NOT_CORRECT(
      1003, "Email hoặc mật khẩu không chính xác", HttpStatus.BAD_REQUEST),
  ACCOUNT_HAS_BEEN_DISABLE(
      1004, "Tài khoản của bạn chưa kích hoạt hoặc bị khóa", HttpStatus.BAD_REQUEST),
  INVALID_TOKEN(1005, "Invalid token", HttpStatus.BAD_REQUEST),
  ROLE_NOT_FOUND(1006, "Role not found", HttpStatus.BAD_REQUEST),
  INCORRECT_PASSWORD(1007, "Current password is incorrect", HttpStatus.BAD_REQUEST),
  PASSWORD_MISMATCH(1008, "New password and confirm password do not match", HttpStatus.BAD_REQUEST),
  ACCESS_DENIED(1009, "Access Deinied", HttpStatus.UNAUTHORIZED),
  INVALID_KEY(1010, "Invalid key", HttpStatus.BAD_REQUEST),
  // PERSON EXCEPTION (1100 - 1199)
  PERSON_NOT_FOUND(1100, "Person not found", HttpStatus.NOT_FOUND),
  COUNTRY_NOT_FOUND(1101, "Country not found", HttpStatus.BAD_REQUEST),
  INVALID_OCCUPATION(1102, "Invalid occupation", HttpStatus.BAD_REQUEST),
  // GENRE EXCEPTION (1200 - 1299)
  GENRE_ALREADY_EXISTS(1200, "Genre name already exists", HttpStatus.BAD_REQUEST),
  GENRE_NOT_FOUND(1201, "Genre not found", HttpStatus.NOT_FOUND),
  GENRE_IN_USE(1202, "Genre in use", HttpStatus.BAD_REQUEST),
  // MOVIE EXCEPTION (1300 - 1399)
  INVALID_COUNTRY(1300, "Invalid country ID", HttpStatus.BAD_REQUEST),
  INVALID_GENRE(1301, "One or more genre IDs are invalid", HttpStatus.BAD_REQUEST),
  INVALID_PERSON(1302, "One or more person IDs are invalid", HttpStatus.BAD_REQUEST),
  MOVIE_NOT_FOUND(1303, "Movie not found", HttpStatus.NOT_FOUND),
  MOVIE_TITLE_ALREADY_EXISTS(1304, "Tiêu đề phim đã tồn tại", HttpStatus.BAD_REQUEST),
  INVALID_PREMIERE_DATE(1305, "Ngày công chiếu phải sau hoặc bằng ngày phát hành", HttpStatus.BAD_REQUEST),
  INVALID_END_DATE(1306, "Ngày kết thúc chiếu phải sau hoặc bằng ngày công chiếu", HttpStatus.BAD_REQUEST),
  MOVIE_MISSING_REQUIRED_FORMAT(1307, "Sai định dạng chiếu", HttpStatus.BAD_REQUEST),
  MOVIE_ALREADY_DELETED(1308, "Movie already deleted", HttpStatus.BAD_REQUEST),
  // COUNTRY EXCEPTION (1400 - 1499)
  COUNTRY_ALREADY_EXISTS(1400, "Country name already exists", HttpStatus.BAD_REQUEST),
  // USER EXCEPTION (1500 - 1599)
  USER_NOT_FOUND(1500, "User not found", HttpStatus.NOT_FOUND),
  USERNAME_ALREADY_EXISTS(1501, "Username already exists", HttpStatus.BAD_REQUEST),
  INVALID_ROLES(1502, "Invalid roles", HttpStatus.BAD_REQUEST),
  MANAGER_CANNOT_ASSIGN_TO_OTHER_CINEMA(
      1503, "Manager cannot assign to other cinema", HttpStatus.BAD_REQUEST),

  // CINEMA EXCEPTION (1200 - 1299);
  CINEMA_NOT_FOUND(2000, "Cinema not found", HttpStatus.NOT_FOUND),
  SOME_CINEMAS_NOT_FOUND(2001, "Some cinemas not found", HttpStatus.BAD_REQUEST),

  // OTP EXCEPTION ()
  SECRET_KEY_INCORRECT(2100, "Secret Key incorrect", HttpStatus.NOT_FOUND),
  SECRET_KEY_EXPIRED(2101, "Secret Key not found or expired", HttpStatus.NOT_FOUND);

  ErrorCode(int code, String message, HttpStatusCode statusCode) {
    this.code = code;
    this.message = message;
    this.statusCode = statusCode;
  }

  private final int code;
  private final String message;
  private final HttpStatusCode statusCode;
}
