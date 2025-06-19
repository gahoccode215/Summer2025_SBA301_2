package com.sba301.online_ticket_sales.dto.review.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieReviewResponse {
  private Long movieId;

  private Long userId;

  private String fullName;

  private Integer rating;

  private String comment;
}
