package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.booking.response.TicketOrderDTO;
import java.util.List;

public interface BookingCacheService {
  void holdSeat(Long showtimeId, List<String> seatCodes, String ticketCode);

  void releaseSeat(Long showtimeId, List<String> seatCode);

  List<String> getHeldSeats(Long showtimeId);

  boolean isSeatHeld(Long showtimeId, String seatCode);

  void saveTicketOrder(TicketOrderDTO ticketOrderDTO);

  TicketOrderDTO getTicketOrder(String ticketCode);

  void deleteTicketOrder(String ticketCode);
}
