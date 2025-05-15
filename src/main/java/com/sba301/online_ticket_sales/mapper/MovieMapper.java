package com.sba301.online_ticket_sales.mapper;

import com.sba301.online_ticket_sales.dto.movie.request.MovieCreationRequest;
import com.sba301.online_ticket_sales.entity.Movie;

public interface MovieMapper {
    Movie toMovie(MovieCreationRequest request);
}
