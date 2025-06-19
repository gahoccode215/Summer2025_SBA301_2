package com.sba301.online_ticket_sales.dto.review.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
  @NotNull(message = "movieId không được để trống")
  private Long movieId;

  @NotNull(message = "rating không được để trống")
  @Min(value = 1, message = "rating phải từ 1 đến 5")
  @Max(value = 5, message = "rating phải từ 1 đến 5")
  private Integer rating;

  private String comment;
}
