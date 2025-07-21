package com.sba301.online_ticket_sales.dto.user.response;

import com.sba301.online_ticket_sales.enums.Gender;
import com.sba301.online_ticket_sales.enums.UserStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserListResponse {

  Long id;
  String fullName;
  String username;
  String email;
  String phone;
  Gender gender;
  UserStatus status;
  LocalDate birthDate;
  String address;

  List<String> roles;
  List<CinemaInfo> workingCinemas;

  LocalDateTime createdAt;
  LocalDateTime updatedAt;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CinemaInfo {
    Long id;
    String name;
    String address;
    String province;
    Boolean isActive;
  }
}
