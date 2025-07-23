package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.entity.TicketOrder;
import com.sba301.online_ticket_sales.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketOrderRepository extends JpaRepository<TicketOrder, Long> {

  @Query(
      value =
          """
            SELECT d.seat_code
            FROM ticket_order_details d
            JOIN ticket_orders o ON d.ticket_order_id = o.id
            WHERE o.movie_showtimes_id = :showtimeId
        """,
      nativeQuery = true)
  List<String> findSeatCodesByShowtimeId(@Param("showtimeId") Long showtimeId);

  @Query(
      value =
          """
        SELECT COUNT(*)
        FROM ticket_orders o
        JOIN ticket_order_details d ON o.id = d.ticket_order_id
        WHERE o.movie_showtimes_id = :showtimeId
          AND d.seat_code = :seatCode
    """,
      nativeQuery = true)
  int countSeatBooked(@Param("showtimeId") Long showtimeId, @Param("seatCode") String seatCode);

  @Query(
      """
    SELECT t.ticketCode, t.totalAmount, t.paymentStatus, t.createdAt,
           ms.movie.title, ms.movie.thumbnailUrl, ms.movie.duration,
           ms.showtime, ms.room.cinema.name, ms.room.name, ms.room.roomType,
           ms.id
    FROM TicketOrder t
    JOIN t.movieScreen ms
    WHERE t.user.id = :userId
    ORDER BY t.createdAt DESC
    """)
  List<Object[]> findTicketHistoryByUserId(@Param("userId") Long userId);

  // Trong TicketOrderDetailRepository hoặc TicketOrderRepository
  @Query(
      "SELECT tod.seatCode FROM TicketOrderDetail tod WHERE tod.ticketOrder.ticketCode = :ticketCode")
  List<String> findSeatCodesByTicketCode(@Param("ticketCode") String ticketCode);

  List<TicketOrder> findByUserIdOrderByCreatedAtDesc(Long userId);

  @Query(
      """
    SELECT COUNT(t) FROM TicketOrder t
    WHERE t.paymentStatus = :status
    AND t.createdAt BETWEEN :startDate AND :endDate
    """)
  Long countByPaymentStatusAndCreatedAtBetween(
      @Param("status") PaymentStatus status,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query(
      """
    SELECT COALESCE(SUM(t.totalAmount), 0) FROM TicketOrder t
    WHERE t.paymentStatus = :status
    AND t.createdAt BETWEEN :startDate AND :endDate
    """)
  BigDecimal sumRevenueByDateRange(
      @Param("status") PaymentStatus status,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query(
      """
    SELECT m.id, m.title, m.thumbnailUrl, COUNT(t.id), SUM(t.totalAmount)
    FROM TicketOrder t
    JOIN t.movieScreen ms
    JOIN ms.movie m
    WHERE t.paymentStatus = :status
    AND t.createdAt BETWEEN :startDate AND :endDate
    GROUP BY m.id, m.title, m.thumbnailUrl
    ORDER BY COUNT(t.id) DESC
    """)
  List<Object[]> findTopMoviesByDateRange(
      @Param("status") PaymentStatus status,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      Pageable pageable);

  @Query(
      """
    SELECT DATE(t.createdAt), SUM(t.totalAmount), COUNT(t.id)
    FROM TicketOrder t
    WHERE t.paymentStatus = :status
    AND DATE(t.createdAt) BETWEEN :startDate AND :endDate
    GROUP BY DATE(t.createdAt)
    ORDER BY DATE(t.createdAt)
    """)
  List<Object[]> getRevenueByDateRange(
      @Param("status") PaymentStatus status,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  @Query(
      """
    SELECT c.id, c.name, COUNT(t.id), SUM(t.totalAmount),
           (COUNT(t.id) * 100.0 / COUNT(ms.id)) as occupancyRate
    FROM TicketOrder t
    JOIN t.movieScreen ms
    JOIN ms.room r
    JOIN r.cinema c
    WHERE t.paymentStatus = :status
    AND t.createdAt BETWEEN :startDate AND :endDate
    GROUP BY c.id, c.name
    ORDER BY SUM(t.totalAmount) DESC
    """)
  List<Object[]> getCinemaPerformanceByDateRange(
      @Param("status") PaymentStatus status,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query(
      """
    SELECT t.ticketCode, u.fullName, m.title, c.name, t.createdAt, t.totalAmount, t.paymentStatus
    FROM TicketOrder t
    JOIN t.user u
    JOIN t.movieScreen ms
    JOIN ms.movie m
    JOIN ms.room r
    JOIN r.cinema c
    ORDER BY t.createdAt DESC
    """)
  List<Object[]> findRecentBookings(Pageable pageable);

  @Query(
      """
    SELECT COUNT(t) FROM TicketOrder t
    JOIN t.movieScreen ms
    JOIN ms.room r
    WHERE r.cinema.id = :cinemaId
    AND t.paymentStatus = :status
    AND t.createdAt BETWEEN :startDate AND :endDate
    """)
  Long countByCinemaAndPaymentStatusAndDateRange(
      @Param("cinemaId") Long cinemaId,
      @Param("status") PaymentStatus status,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query(
      """
    SELECT COALESCE(SUM(t.totalAmount), 0) FROM TicketOrder t
    JOIN t.movieScreen ms
    JOIN ms.room r
    WHERE r.cinema.id = :cinemaId
    AND t.paymentStatus = :status
    AND t.createdAt BETWEEN :startDate AND :endDate
    """)
  BigDecimal sumRevenueByCinemaAndDateRange(
      @Param("cinemaId") Long cinemaId,
      @Param("status") PaymentStatus status,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query(
      """
    SELECT m.id, m.title, m.thumbnailUrl, COUNT(t.id), SUM(t.totalAmount)
    FROM TicketOrder t
    JOIN t.movieScreen ms
    JOIN ms.movie m
    JOIN ms.room r
    WHERE r.cinema.id = :cinemaId
    AND t.paymentStatus = :status
    AND t.createdAt BETWEEN :startDate AND :endDate
    GROUP BY m.id, m.title, m.thumbnailUrl
    ORDER BY COUNT(t.id) DESC
    """)
  List<Object[]> findTopMoviesByCinemaAndDateRange(
      @Param("cinemaId") Long cinemaId,
      @Param("status") PaymentStatus status,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      Pageable pageable);

  @Query(
      """
    SELECT DATE(t.createdAt), SUM(t.totalAmount), COUNT(t.id)
    FROM TicketOrder t
    JOIN t.movieScreen ms
    JOIN ms.room r
    WHERE r.cinema.id = :cinemaId
    AND t.paymentStatus = :status
    AND DATE(t.createdAt) BETWEEN :startDate AND :endDate
    GROUP BY DATE(t.createdAt)
    ORDER BY DATE(t.createdAt)
    """)
  List<Object[]> getRevenueByCinemaAndDateRange(
      @Param("cinemaId") Long cinemaId,
      @Param("status") PaymentStatus status,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  @Query(
      """
    SELECT t.ticketCode, u.fullName, m.title, c.name, t.createdAt, t.totalAmount, t.paymentStatus
    FROM TicketOrder t
    JOIN t.user u
    JOIN t.movieScreen ms
    JOIN ms.movie m
    JOIN ms.room r
    JOIN r.cinema c
    WHERE c.id = :cinemaId
    ORDER BY t.createdAt DESC
    """)
  List<Object[]> findRecentBookingsByCinema(@Param("cinemaId") Long cinemaId, Pageable pageable);

  Optional<TicketOrder> findByTicketCode(String ticketCode);

  @Query(
      "SELECT DISTINCT t FROM TicketOrder t "
          + "LEFT JOIN FETCH t.user u "
          + "LEFT JOIN FETCH t.movieScreen ms "
          + "LEFT JOIN FETCH ms.movie m "
          + "LEFT JOIN FETCH ms.room r "
          + "LEFT JOIN FETCH r.cinema c "
          + "LEFT JOIN FETCH t.ticketDetails td "
          + "WHERE (:ticketCode IS NULL OR :ticketCode = '' OR t.ticketCode LIKE %:ticketCode%)")
  Page<TicketOrder> findAllTicketsWithSearch(
      @Param("ticketCode") String ticketCode, Pageable pageable);

  @Query(
      "SELECT DISTINCT t FROM TicketOrder t "
          + "LEFT JOIN FETCH t.user u "
          + "LEFT JOIN FETCH t.movieScreen ms "
          + "LEFT JOIN FETCH ms.movie m "
          + "LEFT JOIN FETCH ms.room r "
          + "LEFT JOIN FETCH r.cinema c "
          + "LEFT JOIN FETCH t.ticketDetails td "
          + "WHERE c.id = :cinemaId "
          + "AND (:ticketCode IS NULL OR :ticketCode = '' OR t.ticketCode LIKE %:ticketCode%)")
  Page<TicketOrder> findTicketsByCinemaWithSearch(
      @Param("cinemaId") Long cinemaId, @Param("ticketCode") String ticketCode, Pageable pageable);

  @Query(
      "SELECT t FROM TicketOrder t "
          + "LEFT JOIN FETCH t.user u "
          + "LEFT JOIN FETCH t.movieScreen ms "
          + "LEFT JOIN FETCH ms.movie m "
          + "LEFT JOIN FETCH ms.room r "
          + "LEFT JOIN FETCH r.cinema c "
          + "LEFT JOIN FETCH t.ticketDetails td "
          + "WHERE t.ticketCode = :ticketCode")
  Optional<TicketOrder> findByTicketCodeWithDetails(@Param("ticketCode") String ticketCode);

  @Query(
      "SELECT t FROM TicketOrder t "
          + "LEFT JOIN FETCH t.user u "
          + "LEFT JOIN FETCH t.movieScreen ms "
          + "LEFT JOIN FETCH ms.movie m "
          + "LEFT JOIN FETCH ms.room r "
          + "LEFT JOIN FETCH r.cinema c "
          + "LEFT JOIN FETCH t.ticketDetails td "
          + "WHERE t.id = :ticketId")
  Optional<TicketOrder> findByIdWithDetails(@Param("ticketId") Long ticketId);

  @Query(
      "SELECT t FROM TicketOrder t "
          + "JOIN t.movieScreen ms "
          + "JOIN ms.room r "
          + "JOIN r.cinema c "
          + "WHERE c.id IN :cinemaIds "
          + "AND ms.showtime BETWEEN :startTime AND :endTime "
          + "ORDER BY ms.showtime ASC")
  List<TicketOrder> findTicketsByCinemaAndTimeRange(
      @Param("cinemaIds") List<Long> cinemaIds,
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime);
}
