package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.review.request.ReviewRequest;
import com.sba301.online_ticket_sales.dto.review.request.UpdateReviewRequest;
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
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

  private final MovieReviewRepository movieReviewRepository;
  private final MovieRepository movieRepository;

  @Override
  @Transactional
  public MovieReviewResponse addReview(ReviewRequest request) {
    User user = getUserAuthenticated();

    if (movieReviewRepository.existsByUserIdAndMovieId(user.getId(), request.getMovieId())) {
      throw new AppException(ErrorCode.REVIEW_ALREADY_EXISTS);
    }

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

    movieReviewRepository.save(review);
    return MovieReviewResponse.builder()
        .movieId(review.getMovieId())
        .fullName(review.getFullName())
        .rating(review.getRating())
        .comment(review.getComment())
        .userId(review.getUserId())
        .build();
  }

  @Override
  @Transactional
  public MovieReviewResponse updateReview(UpdateReviewRequest request) {
    User user = getUserAuthenticated();
    MovieReview existingReview =
        movieReviewRepository
            .findByIdAndUserId(request.getId(), user.getId())
            .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND_OR_UNAUTHORIZED));

    if (request.getRating() != null) {
      existingReview.setRating(request.getRating());
    }

    if (request.getComment() != null) {
      existingReview.setComment(request.getComment().trim());
    }
    MovieReview updatedReview = movieReviewRepository.save(existingReview);
    return MovieReviewResponse.builder()
        .movieId(updatedReview.getMovieId())
        .fullName(updatedReview.getFullName())
        .rating(updatedReview.getRating())
        .comment(updatedReview.getComment())
        .userId(updatedReview.getUserId())
        .build();
  }

  @Override
  @Transactional
  public void deleteReview(String reviewId) {
    User user = getUserAuthenticated();
    MovieReview review =
        movieReviewRepository
            .findByIdAndUserId(reviewId, user.getId())
            .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND_OR_UNAUTHORIZED));

    movieReviewRepository.delete(review);
  }

  @Override
  public List<MovieReviewResponse> getMovieReviews(Long movieId) {
    List<MovieReview> reviews = movieReviewRepository.findByMovieIdOrderByCreatedAtDesc(movieId);
    return reviews.stream().map(this::mapToResponse).collect(Collectors.toList());
  }

  @Override
  public Double getAverageRating(Long movieId) {
    List<MovieReview> reviews = movieReviewRepository.findByMovieIdOrderByCreatedAtDesc(movieId);
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

  private MovieReviewResponse mapToResponse(MovieReview review) {
    return MovieReviewResponse.builder()
        .id(review.getId())
        .userId(review.getUserId())
        .fullName(review.getFullName())
        .movieId(review.getMovieId())
        .rating(review.getRating())
        .comment(review.getComment())
        .build();
  }
}
