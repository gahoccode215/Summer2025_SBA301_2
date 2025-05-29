package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.movie.request.MovieCreationRequest;
import com.sba301.online_ticket_sales.dto.movie.request.MovieUpdateRequest;
import com.sba301.online_ticket_sales.dto.movie.response.MovieResponse;
import com.sba301.online_ticket_sales.entity.Movie;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.mapper.MovieMapper;
import com.sba301.online_ticket_sales.repository.MovieRepository;
import com.sba301.online_ticket_sales.service.MovieService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieServiceImpl implements MovieService {

    MovieRepository movieRepository;
    MovieMapper movieMapper;

    @Override
    public MovieResponse createMovie(MovieCreationRequest request) {
        Movie movie = movieMapper.toMovie(request);
        Movie savedMovie = movieRepository.save(movie);
        return movieMapper.toMovieResponse(savedMovie);
    }

    @Override
    public MovieResponse updateMovie(Long id, MovieUpdateRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));
        movieMapper.updateMovieFromRequest(request, movie);
        Movie updatedMovie = movieRepository.save(movie);
        log.info("Updated movie: {}", updatedMovie.getTitle());
        return movieMapper.toMovieResponse(updatedMovie);
    }

    @Override
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));
        if (movie.isDeleted()) {
            throw new AppException(ErrorCode.MOVIE_NOT_FOUND);
        }
        movie.getGenres().clear();
        movie.getDirectors().clear();
        movie.getActors().clear();
        movie.setCountry(null);
        movie.setDeleted(true);
        movieRepository.save(movie);
    }

    @Override
    public MovieResponse getMovieDetail(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));
        if (movie.isDeleted()) {
            throw new AppException(ErrorCode.MOVIE_NOT_FOUND);
        }
        return movieMapper.toMovieResponse(movie);
    }
}
