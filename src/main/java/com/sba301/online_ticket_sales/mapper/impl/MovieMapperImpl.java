package com.sba301.online_ticket_sales.mapper.impl;

import com.sba301.online_ticket_sales.dto.country.response.CountryResponse;
import com.sba301.online_ticket_sales.dto.genre.response.GenreResponse;
import com.sba301.online_ticket_sales.dto.movie.request.MovieCreationRequest;
import com.sba301.online_ticket_sales.dto.movie.request.MovieUpdateRequest;
import com.sba301.online_ticket_sales.dto.movie.response.MovieResponse;
import com.sba301.online_ticket_sales.dto.person.response.PersonResponse;
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
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class MovieMapperImpl implements MovieMapper {
  private final CountryRepository countryRepository;
  private final GenreRepository genreRepository;
  private final PersonRepository personRepository;

  public MovieMapperImpl(
      CountryRepository countryRepository,
      GenreRepository genreRepository,
      PersonRepository personRepository) {
    this.countryRepository = countryRepository;
    this.genreRepository = genreRepository;
    this.personRepository = personRepository;
  }

  @Override
  public Movie toMovie(MovieCreationRequest request) {
    Country country = null;
    if (request.getCountryId() != null) {
      country =
          countryRepository
              .findById(request.getCountryId())
              .orElseThrow(() -> new AppException(ErrorCode.INVALID_COUNTRY));
    }

    List<Genre> genres =
        request.getGenreIds() != null
            ? genreRepository.findAllById(request.getGenreIds())
            : List.of();
    if (request.getGenreIds() != null && genres.size() != request.getGenreIds().size()) {
      throw new AppException(ErrorCode.INVALID_GENRE);
    }

    List<Person> directors =
        request.getDirectorIds() != null
            ? personRepository.findAllById(request.getDirectorIds())
            : List.of();
    if (request.getDirectorIds() != null && directors.size() != request.getDirectorIds().size()) {
      throw new AppException(ErrorCode.INVALID_PERSON);
    }

    List<Person> actors =
        request.getActorIds() != null
            ? personRepository.findAllById(request.getActorIds())
            : List.of();
    if (request.getActorIds() != null && actors.size() != request.getActorIds().size()) {
      throw new AppException(ErrorCode.INVALID_PERSON);
    }

    return Movie.builder()
        .title(request.getTitle())
        .description(request.getDescription())
        .ageRestriction(request.getAgeRestriction())
        .duration(request.getDuration())
        .image(request.getImage())
        .releaseDate(request.getReleaseDate())
        .trailerUrl(request.getTrailerUrl())
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
    response.setAgeRestriction(movie.getAgeRestriction());
    response.setTitle(movie.getTitle());
    response.setDescription(movie.getDescription());
    response.setDuration(movie.getDuration());
    response.setReleaseDate(movie.getReleaseDate());
    response.setTrailerUrl(movie.getTrailerUrl());
    response.setMovieStatus(movie.getMovieStatus());
    response.setImage(movie.getImage());
    response.setCountry(movie.getCountry() != null ? toCountryResponse(movie.getCountry()) : null);
    response.setGenres(movie.getGenres().stream().map(this::toGenreResponse).toList());
    response.setDirectors(movie.getDirectors().stream().map(this::toPersonResponse).toList());
    response.setActors(movie.getActors().stream().map(this::toPersonResponse).toList());
    return response;
  }

  @Override
  public void updateMovieFromRequest(MovieUpdateRequest request, Movie movie) {
    Optional.ofNullable(request.getTitle())
        .filter(title -> !title.isBlank())
        .ifPresent(movie::setTitle);
    Optional.ofNullable(request.getDescription()).ifPresent(movie::setDescription);
    Optional.ofNullable(request.getDuration()).ifPresent(movie::setDuration);
    Optional.ofNullable(request.getReleaseDate()).ifPresent(movie::setReleaseDate);
    Optional.ofNullable(request.getTrailerUrl()).ifPresent(movie::setTrailerUrl);
    Optional.ofNullable(request.getMovieStatus()).ifPresent(movie::setMovieStatus);
    Optional.ofNullable(request.getImage()).ifPresent(movie::setImage);
    Optional.ofNullable(request.getAgeRestriction()).ifPresent(movie::setAgeRestriction);

    if (request.getCountryId() != null) {
      Country country =
          countryRepository
              .findById(request.getCountryId())
              .orElseThrow(() -> new AppException(ErrorCode.INVALID_COUNTRY));
      movie.setCountry(country);
    }

    if (request.getGenreIds() != null) {
      List<Genre> genres = genreRepository.findAllById(request.getGenreIds());
      if (genres.size() != request.getGenreIds().size()) {
        throw new AppException(ErrorCode.INVALID_GENRE);
      }
      movie.setGenres(genres);
    }

    if (request.getDirectorIds() != null) {
      List<Person> directors = personRepository.findAllById(request.getDirectorIds());
      if (directors.size() != request.getDirectorIds().size()) {
        throw new AppException(ErrorCode.INVALID_PERSON);
      }
      movie.setDirectors(directors);
    }

    if (request.getActorIds() != null) {
      List<Person> actors = personRepository.findAllById(request.getActorIds());
      if (actors.size() != request.getActorIds().size()) {
        throw new AppException(ErrorCode.INVALID_PERSON);
      }
      movie.setActors(actors);
    }
  }

  private GenreResponse toGenreResponse(Genre genre) {
    GenreResponse response = new GenreResponse();
    response.setId(genre.getId());
    response.setName(genre.getName());
    return response;
  }

  private CountryResponse toCountryResponse(Country country) {
    CountryResponse response = new CountryResponse();
    response.setId(country.getId());
    response.setName(country.getName());
    return response;
  }

  private PersonResponse toPersonResponse(Person person) {
    PersonResponse response = new PersonResponse();
    response.setId(person.getId());
    response.setName(person.getName());
    return response;
  }
}
