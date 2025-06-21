package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.model.MovieReview;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface MovieReviewRepository extends MongoRepository<MovieReview, String> {
  List<MovieReview> findByMovieIdOrderByCreatedAtDesc(Long movieId);

  @Query("{'_id': ?0, 'userId': ?1}")
  Optional<MovieReview> findByIdAndUserId(String id, Long userId);

  boolean existsByUserIdAndMovieId(Long userId, Long movieId);
}
