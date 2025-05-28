package com.sba301.online_ticket_sales.mapper.impl;

import com.sba301.online_ticket_sales.dto.movie.request.MovieCreationRequest;
import com.sba301.online_ticket_sales.dto.movie.request.MovieUpdateRequest;
import com.sba301.online_ticket_sales.dto.movie.response.MovieResponse;
import com.sba301.online_ticket_sales.entity.Country;
import com.sba301.online_ticket_sales.entity.Genre;
import com.sba301.online_ticket_sales.entity.Movie;
import com.sba301.online_ticket_sales.entity.Person;
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

    @Override
    public void updateMovieFromRequest(MovieUpdateRequest request, Movie movie) {
//        Optional.ofNullable(request.getTitle()).ifPresent(movie::setTitle);
//        Optional.ofNullable(request.getDescription()).ifPresent(movie::setDescription);
//        Optional.ofNullable(request.getDuration()).ifPresent(movie::setDuration);
//        Optional.ofNullable(request.getReleaseDate()).ifPresent(movie::setReleaseDate);
//        Optional.ofNullable(request.getTrailerUrl()).ifPresent(movie::setTrailerUrl);
//        Optional.ofNullable(request.getDirector()).ifPresent(movie::setDirector);
//        Optional.ofNullable(request.getMovieStatus()).ifPresent(movie::setMovieStatus);
//        Optional.ofNullable(request.getCountryId()).ifPresent(countryId -> {
//            Country country = countryRepository.findById(countryId)
//                    .orElseThrow(() -> new RuntimeException("Country not found"));
//            movie.setCountry(country);
//        });
//        Optional.ofNullable(request.getGenreIds()).ifPresent(genreIds -> {
//            List<Genre> genres = genreRepository.findAllById(genreIds);
//            movie.setGenres(genres);
//        });
//        Optional.ofNullable(request.getDirectorIds()).ifPresent(directorIds -> {
//            List<Person> directors = personRepository.findAllById(directorIds);
//            movie.setDirectors(directors);
//        });
//        Optional.ofNullable(request.getActorIds()).ifPresent(actorIds -> {
//            List<Person> actors = personRepository.findAllById(actorIds);
//            movie.setActors(actors);
//        });
    }
}
