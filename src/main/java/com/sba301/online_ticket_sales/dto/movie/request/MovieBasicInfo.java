package com.sba301.online_ticket_sales.dto.movie.request;

import com.sba301.online_ticket_sales.enums.AgeRestriction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieBasicInfo {
  private Long id;
  private String title;
  private String thumbnailUrl;
  private Integer duration;
  private AgeRestriction ageRestriction;
}
