package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.booking.request.BookingTicketRequest;
import com.sba301.online_ticket_sales.dto.booking.request.TicketSearchRequest;
import com.sba301.online_ticket_sales.dto.booking.response.*;
import java.util.List;
import org.springframework.data.domain.Page;

public interface BookingService {
  SeatMapResponse getSeatMap(Long movieScreenId);

  BookingSeatResponse bookSeats(BookingTicketRequest bookingTicketRequest);

  BookingSeatResponse bookSeatsByManager(
      BookingTicketRequest bookingTicketRequest, Long cinemaId, Long customerId);

  List<TicketHistoryResponse> getUserTicketHistory();

  Page<TicketListResponse> getAllTickets(TicketSearchRequest searchRequest);

  Page<TicketListResponse> getTicketsByCinema(Long cinemaId, TicketSearchRequest searchRequest);

  TicketDetailResponse getTicketDetail(String ticketCode);

  TicketDetailResponse getTicketDetailById(Long ticketId);

  CheckInResponse checkInTicket(String ticketCode);

  List<TicketListResponse> getTodayTicketsByCinema(Long cinemaId);
}
