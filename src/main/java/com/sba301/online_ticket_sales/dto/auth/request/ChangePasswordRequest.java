package com.sba301.online_ticket_sales.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
//    @NotBlank(message = "Current password is required")
    private String currentPassword;

//    @NotBlank(message = "New password is required")
//    @Size(min = 8, message = "New password must be at least 8 characters")
    private String newPassword;

//    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}
