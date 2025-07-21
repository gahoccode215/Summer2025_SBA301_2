package com.sba301.online_ticket_sales.dto.cinema.response;

import com.sba301.online_ticket_sales.enums.UserStatus;
import java.util.List;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Setter
public class ManagerCinemaResponse {
  private Long userId;
  private String fullName;
  private String email;
  private String phoneNumber;
  private List<String> roles;
  private UserStatus status;
}
