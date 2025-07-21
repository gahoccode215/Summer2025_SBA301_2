package com.sba301.online_ticket_sales.dto.auth.request;

import com.sba301.online_ticket_sales.enums.OTPType;
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
@Getter
@Builder
public class ResendOTPRequest {
  @NotNull(message = "Email is required")
  @NotBlank(message = "Email cannot be blank")
  @Pattern(
      regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
      message = "The email format is incorrect.")
  @Schema(description = "email", example = "email@email.com")
  private String email;

  @NotNull(message = "OTP type is require")
  private OTPType otpType;
}
