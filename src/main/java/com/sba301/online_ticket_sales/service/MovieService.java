package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.movie.request.MovieCreationRequest;
import com.sba301.online_ticket_sales.dto.movie.request.MovieUpdateRequest;
import com.sba301.online_ticket_sales.dto.movie.response.MovieResponse;
import com.sba301.online_ticket_sales.enums.MovieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface MovieService {
  MovieResponse createMovie(MovieCreationRequest request, MultipartFile thumbnailFile);

  MovieResponse updateMovie(Long id, MovieUpdateRequest request, MultipartFile thumbnailFile);

  void deleteMovie(Long id);

  MovieResponse getMovieDetail(Long id);

  Page<MovieResponse> getAllMovies(Pageable pageable, String keyword, MovieStatus movieStatus);
}
