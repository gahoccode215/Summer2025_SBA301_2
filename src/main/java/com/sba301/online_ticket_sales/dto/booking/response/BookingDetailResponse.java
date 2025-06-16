package com.sba301.online_ticket_sales.dto.booking.response;

import com.sba301.online_ticket_sales.dto.moviescreen.response.MovieScreenResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetailResponse {
    private Long id;

    private String seatNumber;

    private BigDecimal ticketPrice;

    private MovieScreenResponse movieScreen;
}
