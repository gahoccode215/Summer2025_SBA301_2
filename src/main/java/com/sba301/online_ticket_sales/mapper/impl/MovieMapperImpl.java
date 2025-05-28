package com.sba301.online_ticket_sales.mapper.impl;

import com.sba301.online_ticket_sales.dto.movie.request.MovieCreationRequest;
import com.sba301.online_ticket_sales.dto.movie.request.MovieUpdateRequest;
import com.sba301.online_ticket_sales.dto.movie.response.MovieResponse;
import com.sba301.online_ticket_sales.entity.Country;
import com.sba301.online_ticket_sales.entity.Genre;
import com.sba301.online_ticket_sales.entity.Movie;
import com.sba301.online_ticket_sales.entity.Person;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.mapper.MovieMapper;
import com.sba301.online_ticket_sales.repository.CountryRepository;
import com.sba301.online_ticket_sales.repository.GenreRepository;
import com.sba301.online_ticket_sales.repository.PersonRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MovieMapperImpl implements MovieMapper {
    private final CountryRepository countryRepository;
    private final GenreRepository genreRepository;
    private final PersonRepository personRepository;

    public MovieMapperImpl(CountryRepository countryRepository, GenreRepository genreRepository, PersonRepository personRepository) {
        this.countryRepository = countryRepository;
        this.genreRepository = genreRepository;
        this.personRepository = personRepository;
    }

    @Override
    public Movie toMovie(MovieCreationRequest request) {
        Country country = null;
        if (request.getCountryId() != null) {
            country = countryRepository.findById(request.getCountryId())
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_COUNTRY));
        }

        List<Genre> genres = request.getGenreIds() != null ?
                genreRepository.findAllById(request.getGenreIds()) : List.of();
        if (request.getGenreIds() != null && genres.size() != request.getGenreIds().size()) {
            throw new AppException(ErrorCode.INVALID_GENRE);
        }

        List<Person> directors = request.getDirectorIds() != null ?
                personRepository.findAllById(request.getDirectorIds()) : List.of();
        if (request.getDirectorIds() != null && directors.size() != request.getDirectorIds().size()) {
            throw new AppException(ErrorCode.INVALID_PERSON);
        }

        List<Person> actors = request.getActorIds() != null ?
                personRepository.findAllById(request.getActorIds()) : List.of();
        if (request.getActorIds() != null && actors.size() != request.getActorIds().size()) {
            throw new AppException(ErrorCode.INVALID_PERSON);
        }

        return Movie.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .duration(request.getDuration())
                .releaseDate(request.getReleaseDate())
                .trailerUrl(request.getTrailerUrl())
                .director(request.getDirector())
                .movieStatus(request.getMovieStatus())
                .country(country)
                .genres(genres)
                .directors(directors)
                .actors(actors)
                .build();
    }

    @Override
    public MovieResponse toMovieResponse(Movie movie) {
        MovieResponse response = new MovieResponse();
        response.setId(movie.getId());
        response.setTitle(movie.getTitle());
        response.setDescription(movie.getDescription());
        response.setDuration(movie.getDuration());
        response.setReleaseDate(movie.getReleaseDate());
        response.setTrailerUrl(movie.getTrailerUrl());
        response.setDirector(movie.getDirector());
        response.setMovieStatus(movie.getMovieStatus());
        response.setCountry(movie.getCountry() != null ? movie.getCountry().getName() : null);
        response.setGenres(movie.getGenres().stream().map(Genre::getName).toList());
        response.setDirectors(movie.getDirectors().stream().map(Person::getName).toList());
        response.setActors(movie.getActors().stream().map(Person::getName).toList());
        return response;
    }

}
