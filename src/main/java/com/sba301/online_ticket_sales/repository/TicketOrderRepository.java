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
}
