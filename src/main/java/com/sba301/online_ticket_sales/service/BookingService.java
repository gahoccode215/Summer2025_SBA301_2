package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.booking.request.BookingTicketRequest;
import com.sba301.online_ticket_sales.dto.booking.response.BookingSeatResponse;
import com.sba301.online_ticket_sales.dto.booking.response.SeatMapResponse;
import com.sba301.online_ticket_sales.dto.booking.response.TicketHistoryResponse;
import java.util.List;

public interface BookingService {
  SeatMapResponse getSeatMap(Long movieScreenId);

  BookingSeatResponse bookSeats(BookingTicketRequest bookingTicketRequest);

  List<TicketHistoryResponse> getUserTicketHistory();
}
