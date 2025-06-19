package com.sba301.online_ticket_sales.model;

import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "movie_reviews")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieReview {
  @Id private String id;
  private Long movieId;
  private Long userId;
  private Integer rating;
  private String fullName;
  private String comment;
  private LocalDateTime createdAt;

}
