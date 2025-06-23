package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.dto.moviescreen.response.CinemaShowtimeDTO;
import com.sba301.online_ticket_sales.dto.moviescreen.response.MovieShowtimeDTO;
import com.sba301.online_ticket_sales.entity.MovieScreen;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieScreenRepository extends JpaRepository<MovieScreen, Long> {
  @Query(
      value =
          """
        SELECT IF(
            EXISTS (
                SELECT 1
                FROM movie_screens ms
                JOIN movies m ON ms.movie_id = m.id
                WHERE ms.room_id = :roomId
                  AND ms.status = 'ACTIVE'
                  AND NOT (
                      :endTime <= ms.showtime
                      OR :startTime >= DATE_ADD(ms.showtime, INTERVAL m.duration MINUTE)
                  )
                LIMIT 1
            ),
            1,
            0
        )
        """,
      nativeQuery = true)
  int hasConflictSchedule(
      @Param("roomId") Long roomId,
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime);

  @Query(
      value =
          """
        SELECT IF(
            EXISTS (
                SELECT 1
                FROM movie_screens ms
                JOIN movies m ON ms.movie_id = m.id
                WHERE ms.room_id = :roomId
                  AND ms.status = 'ACTIVE'
                  AND ms.id != :currentId
                  AND NOT (
                      :endTime <= ms.showtime
                      OR :startTime >= DATE_ADD(ms.showtime, INTERVAL m.duration + 10 MINUTE)
                  )
                LIMIT 1
            ),
            1,
            0
        )
        """,
      nativeQuery = true)
  int hasConflictScheduleExcludingId(
      @Param("currentId") Long currentId,
      @Param("roomId") Long roomId,
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime);

  @Query(
      value =
          """
                  SELECT
                    c.id AS cinema_id,
                    c.name AS cinema_name,
                    c.address AS cinema_address,
                    ms.id AS show_time_id,
                    r.id AS room_id,
                    ms.showtime AS show_time,
                    r.room_type AS room_type
                  FROM movie_screens ms
                  JOIN movies m ON ms.movie_id = m.id
                  JOIN rooms r ON ms.room_id = r.id
                  JOIN cinemas c ON r.cinema_id = c.id
                  WHERE m.id = :movieId
                    AND ms.showtime >= :dateTime
                    AND ms.status = 'ACTIVE'
                  ORDER BY c.id, ms.showtime
                  """,
      nativeQuery = true)
  List<CinemaShowtimeDTO> findShowTimesByMovie(
      @Param("movieId") Long movieId, @Param("dateTime") LocalDateTime dateTime);

  @Query(
      value =
          """
                  SELECT
                    ms.id AS showTimeId,
                    r.id AS roomId,
                    r.room_type AS roomType,
                    ms.showtime AS showTime,
                    m.id AS movieId,
                    m.title AS movieName,
                    m.thumbnail_url AS moviePosterUrl,
                    m.duration AS movieDuration,
                    m.age_restriction AS movieRating,
                    m.release_date AS movieReleaseDate
                  FROM movie_screens ms
                  JOIN rooms r ON ms.room_id = r.id
                  JOIN movies m ON ms.movie_id = m.id
                  WHERE r.cinema_id = :cinemaId
                    AND ms.status = 'ACTIVE'
                    AND ms.showtime >= :queryTime
                    AND ms.showtime < :upperBound
                  ORDER BY m.id, ms.showtime
                  """,
      nativeQuery = true)
  List<MovieShowtimeDTO> findByCinemaAndDate(
      @Param("cinemaId") Long cinemaId,
      @Param("queryTime") LocalDateTime queryTime,
      @Param("upperBound") LocalDateTime upperBound);
}
