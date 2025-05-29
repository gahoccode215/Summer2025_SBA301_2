package com.sba301.online_ticket_sales.dto.user.request;

import com.sba301.online_ticket_sales.enums.Gender;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileUpdateRequest {
  private String fullName;

  private LocalDate birthDate;

  private Gender gender;

  private String phone;
}
