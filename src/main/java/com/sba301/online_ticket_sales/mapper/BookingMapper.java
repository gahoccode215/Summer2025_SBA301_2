package com.sba301.online_ticket_sales.mapper;

import com.sba301.online_ticket_sales.dto.booking.response.BookingResponse;
import com.sba301.online_ticket_sales.entity.Booking;

public interface BookingMapper {
    BookingResponse toResponse(Booking booking);
}
