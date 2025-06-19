package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.review.request.ReviewRequest;
import com.sba301.online_ticket_sales.dto.review.response.MovieReviewResponse;
import com.sba301.online_ticket_sales.entity.Movie;
import com.sba301.online_ticket_sales.entity.User;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.model.MovieReview;
import com.sba301.online_ticket_sales.repository.MovieRepository;
import com.sba301.online_ticket_sales.repository.MovieReviewRepository;
import com.sba301.online_ticket_sales.service.ReviewService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

  private final MovieReviewRepository reviewRepository;
  private final MovieRepository movieRepository;

  @Override
  @Transactional
  public MovieReviewResponse addReview(ReviewRequest request) {
    User user = getUserAuthenticated();

    Movie movie =
        movieRepository
            .findByIdAndIsDeleted(request.getMovieId(), false)
            .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));
    MovieReview review =
        MovieReview.builder()
            .movieId(request.getMovieId())
            .userId(user.getId())
            .rating(request.getRating())
            .fullName(user.getFullName())
            .comment(request.getComment())
            .createdAt(LocalDateTime.now())
            .build();

    reviewRepository.save(review);
    return MovieReviewResponse.builder()
        .movieId(review.getMovieId())
        .fullName(review.getFullName())
        .rating(review.getRating())
        .comment(review.getComment())
        .userId(review.getUserId())
        .build();
  }

  @Override
  public List<MovieReview> getMovieReviews(Long movieId) {
    return reviewRepository.findByMovieIdOrderByCreatedAtDesc(movieId);
  }

  @Override
  public Double getAverageRating(Long movieId) {
    List<MovieReview> reviews = reviewRepository.findByMovieIdOrderByCreatedAtDesc(movieId);
    return reviews.stream().mapToInt(MovieReview::getRating).average().orElse(0.0);
  }

  private User getUserAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication.getPrincipal() instanceof String) {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }
    return (User) authentication.getPrincipal();
  }
}
