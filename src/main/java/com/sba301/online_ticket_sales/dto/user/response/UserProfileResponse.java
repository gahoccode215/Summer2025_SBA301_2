package com.sba301.online_ticket_sales.dto.user.response;

import com.sba301.online_ticket_sales.enums.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class UserProfileResponse {
    private Long id;
    private String email;
    private String fullName;
    private LocalDate birthDate;
    private Gender gender;
    private List<String> roles;
}
