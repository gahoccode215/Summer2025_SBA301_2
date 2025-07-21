package com.sba301.online_ticket_sales.dto.user.request;

import com.sba301.online_ticket_sales.dto.cinema.response.CinemaResponse;
import com.sba301.online_ticket_sales.enums.Gender;
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
public class CreateUserAccountRequest {

  @NotBlank(message = "Họ tên không được để trống")
  @Size(min = 2, max = 100, message = "Họ tên phải từ 2-100 ký tự")
  String fullName;

  String username;

  String phone;

  String password;

  @NotNull(message = "Giới tính không được để trống")
  Gender gender;

  @Past(message = "Ngày sinh phải là ngày trong quá khứ")
  LocalDate birthDate;

  String address;

  @NotEmpty(message = "Phải chọn ít nhất một vai trò")
  List<Long> roleIds;

  // Chỉ áp dụng cho MANAGER và STAFF_ROLE - rạp được gán
  List<Long> assignedCinemaIds;

  List<CinemaResponse> workingCinemas;
}
