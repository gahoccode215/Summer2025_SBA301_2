package com.sba301.online_ticket_sales.dto.auth.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ResetPasswordRequest {

    @NotNull(message = "Email is required")
    @NotBlank(message = "Email cannot be blank")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "The email format is incorrect.")
    @Schema(description = "email", example = "email@email.com")
    private String email;

    @NotNull(message = "New password is required")
    @NotBlank(message = "New password cannot be blank")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message =
                    "The new password must be at least 8 characters, including letters, numbers, and special characters.")
    @Schema(description = "password", example = "NguyenThanhSr4@")
    private String newPassword;

    @NotNull(message = "Confirm password is required")
    @NotBlank(message = "Confirm password cannot be blank")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message =
                    "The confirm password must be at least 8 characters, including letters, numbers, and special characters.")
    @Schema(description = "password", example = "NguyenThanhSr4@")
    private String confirmPassword;

    @NotNull(message = "Reset key is required")
    @NotBlank(message = "Reset key cannot be blank")
    @Schema(description = "resetKey", example = "1234567890abcdef")
    private String resetKey;
}

