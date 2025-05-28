package com.sba301.online_ticket_sales.mapper;

import com.sba301.online_ticket_sales.dto.movie.request.MovieCreationRequest;
import com.sba301.online_ticket_sales.dto.movie.request.MovieUpdateRequest;
import com.sba301.online_ticket_sales.dto.movie.response.MovieResponse;
import com.sba301.online_ticket_sales.entity.Movie;

public interface MovieMapper {
    Movie toMovie(MovieCreationRequest request);
    MovieResponse toMovieResponse(Movie movie);
    void updateMovieFromRequest(MovieUpdateRequest request, Movie movie);
}
