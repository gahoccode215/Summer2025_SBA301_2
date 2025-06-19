package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.review.request.ReviewRequest;
import com.sba301.online_ticket_sales.dto.review.response.MovieReviewResponse;
import com.sba301.online_ticket_sales.model.MovieReview;
import java.util.List;

public interface ReviewService {
  MovieReviewResponse addReview(ReviewRequest request);

  List<MovieReview> getMovieReviews(Long movieId);

  Double getAverageRating(Long movieId);
}
