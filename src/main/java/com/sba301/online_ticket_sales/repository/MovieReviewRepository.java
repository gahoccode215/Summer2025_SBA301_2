package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.model.MovieReview;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface MovieReviewRepository extends MongoRepository<MovieReview, String> {
  List<MovieReview> findByMovieIdOrderByCreatedAtDesc(Long movieId);

  List<MovieReview> findByUserId(Long userId);

  @Query("{'movieId': ?0}")
  List<MovieReview> findReviewsByMovie(Long movieId);
}
