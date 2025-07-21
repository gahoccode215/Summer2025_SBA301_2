package com.sba301.online_ticket_sales.dto.user.request;

import com.sba301.online_ticket_sales.enums.Gender;
import com.sba301.online_ticket_sales.enums.UserStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {

  @Size(min = 2, max = 100, message = "Họ tên phải từ 2-100 ký tự")
  String fullName;

  String phone;

  Gender gender;

  @Past(message = "Ngày sinh phải là ngày trong quá khứ")
  LocalDate birthDate;

  String address;

  UserStatus status;

  List<Integer> roleIds;

  List<Long> assignedCinemaIds;

  public boolean hasUpdates() {
    return fullName != null
        || phone != null
        || gender != null
        || birthDate != null
        || address != null
        || status != null
        || roleIds != null
        || assignedCinemaIds != null;
  }

  // Helper method để check có update critical fields không
  public boolean hasCriticalUpdates() {
    return status != null || roleIds != null;
  }
}
