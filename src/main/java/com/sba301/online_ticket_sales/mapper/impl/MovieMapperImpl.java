package com.sba301.online_ticket_sales.mapper.impl;

import com.sba301.online_ticket_sales.dto.request.MovieCreationRequest;
import com.sba301.online_ticket_sales.entity.Movie;
import com.sba301.online_ticket_sales.mapper.MovieMapper;
import org.springframework.stereotype.Component;

@Component
public class MovieMapperImpl implements MovieMapper {
    @Override
    public Movie toMovie(MovieCreationRequest request) {
        return Movie.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .duration(request.getDuration())
                .director(request.getDirector())
                .trailerUrl(request.getTrailerUrl())
                .releaseDate(request.getReleaseDate())
                .movieStatus(request.getMovieStatus())
                .build();
    }
}
