package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.movie.request.MovieCreationRequest;

public interface MovieService {
    void createMovie(MovieCreationRequest request);
//    MovieResponse updateMovie(Long id, MovieUpdateRequest request);
//    void deleteMovie(Long id);
//    List<MovieResponse> getAllMovies(Pageable pageable);
//    MovieResponse getMovieDetail(Long id);
}
