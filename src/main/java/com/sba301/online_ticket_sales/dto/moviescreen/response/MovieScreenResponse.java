package com.sba301.online_ticket_sales.dto.moviescreen.response;

import com.sba301.online_ticket_sales.dto.cinema.response.CinemaBasicInfo;
import com.sba301.online_ticket_sales.dto.movie.request.MovieBasicInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieScreenResponse {
    private Long id;

    private String screenRoom;

    private String screenDate;

    private String screenTime;

    private BigDecimal price;

    private Integer availableSeats;

    private MovieBasicInfo movie;

    private CinemaBasicInfo cinema;

}
