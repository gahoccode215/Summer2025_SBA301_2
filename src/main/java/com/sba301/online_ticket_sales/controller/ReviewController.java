package com.sba301.online_ticket_sales.controller;

import com.sba301.online_ticket_sales.dto.common.ApiResponseDTO;
import com.sba301.online_ticket_sales.dto.review.request.ReviewRequest;
import com.sba301.online_ticket_sales.dto.review.request.UpdateReviewRequest;
import com.sba301.online_ticket_sales.dto.review.response.MovieReviewResponse;
import com.sba301.online_ticket_sales.model.MovieReview;
import com.sba301.online_ticket_sales.service.ReviewService;
import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {
  private final ReviewService reviewService;

  @PostMapping
  @PreAuthorize("hasRole('CUSTOMER')")
  public ResponseEntity<ApiResponseDTO<MovieReviewResponse>> addReview(
      @RequestBody @Valid ReviewRequest request) {
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
  public ResponseEntity<ApiResponseDTO<List<MovieReviewResponse>>> getMovieReviews(
          @PathVariable Long movieId) {
    List<MovieReviewResponse> reviews = reviewService.getMovieReviews(movieId);
    return ResponseEntity.ok(
            ApiResponseDTO.<List<MovieReviewResponse>>builder()
                    .code(HttpStatus.OK.value())
                    .message("Lấy danh sách đánh giá thành công")
                    .result(reviews)
                    .build());
  }
  @GetMapping("/movie/{movieId}/average")
  public ResponseEntity<ApiResponseDTO<Double>> getAverageRating(@PathVariable Long movieId) {
    Double averageRating = reviewService.getAverageRating(movieId);
    return ResponseEntity.ok(
            ApiResponseDTO.<Double>builder()
                    .code(HttpStatus.OK.value())
                    .message("Lấy điểm trung bình thành công")
                    .result(averageRating)
                    .build());
  }
  @PutMapping
  @PreAuthorize("hasAnyRole('CUSTOMER')")
  public ResponseEntity<ApiResponseDTO<MovieReviewResponse>> updateReview(
          @Valid @RequestBody UpdateReviewRequest request) {
    MovieReviewResponse updatedReview = reviewService.updateReview(request);
    return ResponseEntity.ok(
            ApiResponseDTO.<MovieReviewResponse>builder()
                    .code(HttpStatus.OK.value())
                    .message("Cập nhật đánh giá thành công")
                    .result(updatedReview)
                    .build());
  }

  @DeleteMapping("/{reviewId}")
  @PreAuthorize("hasAnyRole('CUSTOMER')")
  public ResponseEntity<ApiResponseDTO<Void>> deleteReview(@PathVariable String reviewId) {
    reviewService.deleteReview(reviewId);
    return ResponseEntity.ok(
            ApiResponseDTO.<Void>builder()
                    .code(HttpStatus.OK.value())
                    .message("Xóa đánh giá thành công")
                    .build());
  }


}
