package com.sba301.online_ticket_sales.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuickCustomerRequest {

  @NotBlank(message = "Số điện thoại không được để trống")
  //    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Số điện thoại không hợp lệ")
  private String phone;

  @NotBlank(message = "Tên đầy đủ không được để trống")
  private String fullName;
}
