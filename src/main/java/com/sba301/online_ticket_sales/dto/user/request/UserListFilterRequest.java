package com.sba301.online_ticket_sales.dto.user.request;

import com.sba301.online_ticket_sales.enums.UserStatus;
import java.time.LocalDate;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserListFilterRequest {

  String keyword; // Tìm kiếm theo username/email/fullName
  UserStatus status; // ACTIVE, INACTIVE
  String roleName; // ADMIN, MANAGER, STAFF_ROLE, CUSTOMER
  Long cinemaId; // Lọc theo rạp
  String province; // Lọc theo tỉnh/thành phố của rạp
  LocalDate createdFrom; // Tài khoản tạo từ ngày
  LocalDate createdTo; // Tài khoản tạo đến ngày
}
