package com.sba301.online_ticket_sales.dto.auth.request;

import com.sba301.online_ticket_sales.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
  @NotBlank(message = "FullName can not blank")
  String fullName;

  @NotBlank(message = "Email can not blank")
  String email;

  @NotNull(message = "Gender can not null")
  Gender gender;

  @NotNull(message = "Birthdate can not null")
  LocalDate birthDate;

  @NotBlank(message = "Password can not blank")
  String password;
}
