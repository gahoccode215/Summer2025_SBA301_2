package com.sba301.online_ticket_sales.controller;

import com.sba301.online_ticket_sales.dto.common.ApiResponseDTO;
import com.sba301.online_ticket_sales.dto.review.request.ReviewRequest;
import com.sba301.online_ticket_sales.dto.review.response.MovieReviewResponse;
import com.sba301.online_ticket_sales.model.MovieReview;
import com.sba301.online_ticket_sales.service.ReviewService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {
  private final ReviewService reviewService;

  @PostMapping
  public ResponseEntity<ApiResponseDTO<MovieReviewResponse>> addReview(
      @RequestBody ReviewRequest request) {
    MovieReviewResponse movieReviewResponse = reviewService.addReview(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            ApiResponseDTO.<MovieReviewResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Đánh giá thành công")
                .result(movieReviewResponse)
                .build());
  }

  @GetMapping("/movie/{movieId}")
  public ResponseEntity<List<MovieReview>> getMovieReviews(@PathVariable Long movieId) {
    return ResponseEntity.ok(reviewService.getMovieReviews(movieId));
  }

  @GetMapping("/movie/{movieId}/average")
  public ResponseEntity<Double> getAverageRating(@PathVariable Long movieId) {
    return ResponseEntity.ok(reviewService.getAverageRating(movieId));
  }
}
