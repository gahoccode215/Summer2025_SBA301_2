package com.sba301.online_ticket_sales.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
  UNCATEGORIZED_EXCEPTION(9999, "Lỗi không phân loại", HttpStatus.INTERNAL_SERVER_ERROR),
  // AUTHENTICATION EXCEPTION (1000 - 1099)
  UNAUTHENTICATED(1000, "Vui lòng đăng nhập để sử dụng chức năng này", HttpStatus.UNAUTHORIZED),
  UNAUTHORIZED(1001, "Không thể thực hiện chức năng này", HttpStatus.FORBIDDEN),
  EMAIL_ALREADY_EXISTS(1002, "Email đã tồn tại", HttpStatus.BAD_REQUEST),
  EMAIL_OR_PASSWORD_NOT_CORRECT(
      1003, "Email hoặc mật khẩu không chính xác", HttpStatus.BAD_REQUEST),
  ACCOUNT_HAS_BEEN_DISABLE(
      1004, "Tài khoản của bạn chưa kích hoạt hoặc bị khóa", HttpStatus.BAD_REQUEST),
  INVALID_TOKEN(1005, "Mã thông báo không hợp lệ", HttpStatus.BAD_REQUEST),
  ROLE_NOT_FOUND(1006, "Vai trò không tìm thấy", HttpStatus.BAD_REQUEST),
  INCORRECT_PASSWORD(1007, "Mật khẩu hiện tại không chính xác", HttpStatus.BAD_REQUEST),
  PASSWORD_MISMATCH(1008, "Mật khẩu mới và mật khẩu xác nhận không khớp", HttpStatus.BAD_REQUEST),
  ACCESS_DENIED(1009, "Truy cập bị từ chối", HttpStatus.UNAUTHORIZED),
  INVALID_KEY(1010, "Khóa không hợp lệ", HttpStatus.BAD_REQUEST),
  INSUFFICIENT_PERMISSION(1011, "Quyền truy cập không đủ", HttpStatus.BAD_REQUEST),
  REQUIRE_OTP_VALIDATION(
      1012, "Yêu cầu xác thực OTP. Vui lòng kiểm tra email của bạn", HttpStatus.UNAUTHORIZED),
  // PERSON EXCEPTION (1100 - 1199)
  PERSON_NOT_FOUND(1100, "Người không tìm thấy", HttpStatus.NOT_FOUND),
  COUNTRY_NOT_FOUND(1101, "Quốc gia không tìm thấy", HttpStatus.BAD_REQUEST),
  INVALID_OCCUPATION(1102, "Nghề nghiệp không hợp lệ", HttpStatus.BAD_REQUEST),
  // GENRE EXCEPTION (1200 - 1299)
  GENRE_ALREADY_EXISTS(1200, "Tên thể loại đã tồn tại", HttpStatus.BAD_REQUEST),
  GENRE_NOT_FOUND(1201, "Thể loại không tìm thấy", HttpStatus.NOT_FOUND),
  GENRE_IN_USE(1202, "Thể loại đang được sử dụng, không thể xóa", HttpStatus.BAD_REQUEST),
  // MOVIE EXCEPTION (1300 - 1399)
  INVALID_COUNTRY(1300, "ID quốc gia không hợp lệ", HttpStatus.BAD_REQUEST),
  INVALID_GENRE(1301, "Một hoặc nhiều ID thể loại không hợp lệ", HttpStatus.BAD_REQUEST),
  INVALID_PERSON(1302, "Một hoặc nhiều ID người không hợp lệ", HttpStatus.BAD_REQUEST),
  MOVIE_NOT_FOUND(1303, "Phim không tìm thấy", HttpStatus.NOT_FOUND),
  MOVIE_TITLE_ALREADY_EXISTS(1304, "Tiêu đề phim đã tồn tại", HttpStatus.BAD_REQUEST),
  INVALID_PREMIERE_DATE(
      1305, "Ngày công chiếu phải sau hoặc bằng ngày phát hành", HttpStatus.BAD_REQUEST),
  INVALID_END_DATE(
      1306, "Ngày kết thúc chiếu phải sau hoặc bằng ngày công chiếu", HttpStatus.BAD_REQUEST),
  MOVIE_MISSING_REQUIRED_FORMAT(1307, "Sai định dạng chiếu", HttpStatus.BAD_REQUEST),
  MOVIE_ALREADY_DELETED(1308, "Phim đã bị xóa", HttpStatus.BAD_REQUEST),
  // COUNTRY EXCEPTION (1400 - 1499)
  COUNTRY_ALREADY_EXISTS(1400, "Tên quốc gia đã tồn tại", HttpStatus.BAD_REQUEST),
  REVIEW_NOT_FOUND_OR_UNAUTHORIZED(
      1401, "Đánh giá không tìm thấy hoặc không được phép", HttpStatus.BAD_REQUEST),
  REVIEW_ALREADY_EXISTS(1402, "Đánh giá đã tồn tại", HttpStatus.BAD_REQUEST),
  // USER EXCEPTION (1500 - 1599)
  USER_NOT_FOUND(1500, "Người dùng không tìm thấy", HttpStatus.NOT_FOUND),
  USERNAME_ALREADY_EXISTS(1501, "Tên người dùng đã tồn tại", HttpStatus.BAD_REQUEST),
  INVALID_ROLES(1502, "Vai trò không hợp lệ", HttpStatus.BAD_REQUEST),
  MANAGER_CANNOT_ASSIGN_TO_OTHER_CINEMA(
      1503, "Quản lý không thể gán cho rạp khác", HttpStatus.BAD_REQUEST),
  CANNOT_DISABLE_SELF(1504, "Không thể vô hiệu hóa chính mình", HttpStatus.BAD_REQUEST),
  CANNOT_MODIFY_ADMIN(1505, "Không thể sửa đổi quản trị viên", HttpStatus.BAD_REQUEST),
  PHONE_ALREADY_EXISTS(1506, "Số điện thoại đã tồn tại", HttpStatus.BAD_REQUEST),
  QUICK_ACCOUNT_CANNOT_LOGIN(
      1507, "Tài khoản tạo nhanh không thể đăng nhập", HttpStatus.BAD_REQUEST),
  INVALID_ACCOUNT_TYPE(1508, "Loại tài khoản không hợp lệ", HttpStatus.BAD_REQUEST),
  ACCOUNT_ALREADY_UPGRADED(1509, "Tài khoản đã được nâng cấp", HttpStatus.BAD_REQUEST),
  // IMAGE EXCEPTION (1600 - 1699)
  IMAGE_UPLOAD_FAILED(1600, "Tải lên hình ảnh thất bại", HttpStatus.BAD_REQUEST),
  INVALID_IMAGE_FILE(1601, "Tệp hình ảnh không hợp lệ", HttpStatus.BAD_REQUEST),
  INVALID_IMAGE_FORMAT(1602, "Định dạng hình ảnh không hợp lệ", HttpStatus.BAD_REQUEST),
  FILE_TOO_LARGE(1603, "Tệp quá lớn", HttpStatus.BAD_REQUEST),
  UNSUPPORTED_IMAGE_FORMAT(1604, "Định dạng hình ảnh không được hỗ trợ", HttpStatus.BAD_REQUEST),
  // BOOKING EXCEPTION (1700 - 1799)
  TICKET_CANNOT_NULL(1700, "Danh sách vé không được để trống", HttpStatus.BAD_REQUEST),
  TICKET_DUPLICATE_SEAT(1701, "Danh sách vé có ghế trùng lặp", HttpStatus.BAD_REQUEST),
  SEAT_CANNOT_NULL(1702, "Số ghế không được để trống", HttpStatus.BAD_REQUEST),
  INVALID_SEAT_CODE(1703, "Định dạng số ghế không hợp lệ", HttpStatus.BAD_REQUEST),
  SEAT_CANNOT_FOUND(1704, "Một hoặc nhiều suất chiếu không tồn tại", HttpStatus.BAD_REQUEST),
  MOVIESCREEN_NOT_WORKING(1705, "Suất chiếu không hoạt động", HttpStatus.BAD_REQUEST),

  // CINEMA EXCEPTION (1200 - 1299);
  CINEMA_NOT_FOUND(2000, "Rạp không tìm thấy", HttpStatus.NOT_FOUND),
  SOME_CINEMAS_NOT_FOUND(2001, "Một số rạp không tìm thấy", HttpStatus.BAD_REQUEST),
  CINEMA_UPSERT_PERMISSION_DENIED(
      2002, "Bạn không có quyền tạo hoặc cập nhật rạp", HttpStatus.FORBIDDEN),
  ROOM_NOT_FOUND(2004, "Phòng không tìm thấy", HttpStatus.NOT_FOUND),
  ROOM_OR_MOVIE_NOT_ACTIVE(2005, "Phòng hoặc phim không hoạt động", HttpStatus.BAD_REQUEST),

  // OTP EXCEPTION ()
  SECRET_KEY_INCORRECT(2100, "Khóa bí mật không chính xác", HttpStatus.NOT_FOUND),
  SECRET_KEY_EXPIRED(2101, "Khóa bí mật không tìm thấy hoặc hết hạn", HttpStatus.NOT_FOUND),

  // SCHEDULE EXCEPTION (2200 - 2299)
  SCHEDULE_NOT_FOUND(2200, "Lịch chiếu không tìm thấy", HttpStatus.NOT_FOUND),
  SCHEDULE_ALREADY_EXISTS(2201, "Lịch chiếu đã tồn tại", HttpStatus.BAD_REQUEST),
  SCHEDULE_NOT_ACTIVE(2202, "Lịch chiếu không hoạt động", HttpStatus.BAD_REQUEST),
  SCHEDULE_NO_PERMISSION(
      2203, "Bạn không có quyền tạo hoặc cập nhật lịch chiếu", HttpStatus.FORBIDDEN),

  // BOOKING
  TICKET_ORDER_NOT_FOUND_CACHE(
      8000, "Đơn hàng vé không tìm thấy trong bộ nhớ đệm", HttpStatus.NOT_FOUND),
  TICKET_PRICE_NOT_FOUND(
      8001, "Giá vé không tìm thấy cho rạp và loại ngày đã cho", HttpStatus.NOT_FOUND),
  SEAT_ALREADY_BOOKED(8002, "Ghế đã được đặt cho suất chiếu này", HttpStatus.BAD_REQUEST),
  INVALID_TICKET_PRICE(8003, "Giá vé không thể âm", HttpStatus.BAD_REQUEST),
  FAILED_TO_CREATE_ORDER(8004, "Tạo đơn hàng vé thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
  NO_PERMISSION_TO_BOOK(8005, "Bạn không có quyền đặt vé cho suất chiếu này", HttpStatus.FORBIDDEN),

  // PAYMENT EXCEPTION (9000 - 9099)
  PAYMENT_ERROR(9000, "Lỗi thanh toán", HttpStatus.BAD_REQUEST),
  PAYMENT_FAILED_OR_EXPIRED(9001, "Thanh toán thất bại hoặc hết hạn", HttpStatus.BAD_REQUEST);

  ErrorCode(int code, String message, HttpStatusCode statusCode) {
    this.code = code;
    this.message = message;
    this.statusCode = statusCode;
  }

  private final int code;
  private final String message;
  private final HttpStatusCode statusCode;
}
