package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.booking.request.BookingCreationRequest;
import com.sba301.online_ticket_sales.dto.booking.response.BookingResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface BookingService {
    BookingResponse createBooking(BookingCreationRequest request, Long userId, HttpServletRequest httpRequest);
}
