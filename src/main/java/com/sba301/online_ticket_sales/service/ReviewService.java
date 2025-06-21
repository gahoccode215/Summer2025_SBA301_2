package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.review.request.ReviewRequest;
import com.sba301.online_ticket_sales.dto.review.request.UpdateReviewRequest;
import com.sba301.online_ticket_sales.dto.review.response.MovieReviewResponse;
import java.util.List;

public interface ReviewService {
  MovieReviewResponse addReview(ReviewRequest request);

  MovieReviewResponse updateReview(UpdateReviewRequest request);

  void deleteReview(String reviewId);

  List<MovieReviewResponse> getMovieReviews(Long movieId);

  Double getAverageRating(Long movieId);
}
