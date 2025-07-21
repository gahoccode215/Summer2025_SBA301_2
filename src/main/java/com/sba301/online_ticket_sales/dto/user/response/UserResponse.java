package com.sba301.online_ticket_sales.dto.user.response;

import com.sba301.online_ticket_sales.dto.cinema.response.CinemaResponse;
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
public class UserResponse {
  Long id;
  String fullName;
  String email;
  String username;
  String password;
  String phone;
  Gender gender;
  LocalDate birthDate;
  String address;
  UserStatus status;

  List<String> roles;
  List<CinemaResponse> workingCinemas;

  LocalDateTime createdAt;
  LocalDateTime updatedAt;
}
