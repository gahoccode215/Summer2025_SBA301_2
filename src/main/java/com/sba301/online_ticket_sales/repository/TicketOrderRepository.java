package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.entity.TicketOrder;
import java.util.List;
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
}
