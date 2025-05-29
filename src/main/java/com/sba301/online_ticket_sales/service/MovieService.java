package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.movie.request.MovieCreationRequest;
import com.sba301.online_ticket_sales.dto.movie.request.MovieUpdateRequest;
import com.sba301.online_ticket_sales.dto.movie.response.MovieResponse;

public interface MovieService {
    MovieResponse createMovie(MovieCreationRequest request);
    MovieResponse updateMovie(Long id, MovieUpdateRequest request);
}
