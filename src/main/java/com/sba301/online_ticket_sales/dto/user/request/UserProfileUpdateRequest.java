package com.sba301.online_ticket_sales.dto.user.request;

import com.sba301.online_ticket_sales.enums.Gender;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserProfileUpdateRequest {
    private String fullName;

    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    private Gender gender;

    private String phone;
}
