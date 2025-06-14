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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovieMapperImpl implements MovieMapper {
  private final CountryRepository countryRepository;
  private final GenreRepository genreRepository;
  private final PersonRepository personRepository;


  @Override
  public Movie toMovie(MovieCreationRequest request) {
    // Validate và lấy Country
    Country country = null;
    if (request.getCountryId() != null) {
      country = countryRepository
              .findById(request.getCountryId())
              .orElseThrow(() -> new AppException(ErrorCode.INVALID_COUNTRY));
    }

    // Validate và lấy Genres
    List<Genre> genres = List.of();
    if (request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
      genres = genreRepository.findAllById(request.getGenreIds());
      if (genres.size() != request.getGenreIds().size()) {
        throw new AppException(ErrorCode.INVALID_GENRE);
      }
    }

    // Validate và lấy Directors
    List<Person> directors = List.of();
    if (request.getDirectorIds() != null && !request.getDirectorIds().isEmpty()) {
      directors = personRepository.findAllById(request.getDirectorIds());
      if (directors.size() != request.getDirectorIds().size()) {
        throw new AppException(ErrorCode.INVALID_PERSON);
      }
    }

    // Validate và lấy Actors
    List<Person> actors = List.of();
    if (request.getActorIds() != null && !request.getActorIds().isEmpty()) {
      actors = personRepository.findAllById(request.getActorIds());
      if (actors.size() != request.getActorIds().size()) {
        throw new AppException(ErrorCode.INVALID_PERSON);
      }
    }

    return Movie.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .duration(request.getDuration())
            .releaseDate(request.getReleaseDate())
            .premiereDate(request.getPremiereDate())
            .endDate(request.getEndDate())
            .thumbnailUrl(request.getThumbnailUrl())
            .trailerUrl(request.getTrailerUrl())
            .movieStatus(request.getMovieStatus())
            .ageRestriction(request.getAgeRestriction())
            .availableFormats(request.getAvailableFormats())
            .isDeleted(false)
            .isPublished(true)
            .country(country)
            .genres(genres)
            .directors(directors)
            .actors(actors)
            .build();
  }

  @Override
  public MovieResponse toMovieResponse(Movie movie) {
    return MovieResponse.builder()
            .id(movie.getId())
            .title(movie.getTitle())
            .description(movie.getDescription())
            .duration(movie.getDuration())
            .releaseDate(movie.getReleaseDate())
            .premiereDate(movie.getPremiereDate())
            .endDate(movie.getEndDate())
            .thumbnailUrl(movie.getThumbnailUrl())
            .trailerUrl(movie.getTrailerUrl())
            .movieStatus(movie.getMovieStatus())
            .ageRestriction(movie.getAgeRestriction())
            .availableFormats(movie.getAvailableFormats())
            .isDeleted(movie.getIsDeleted())
            .isPublished(movie.getIsPublished())
            .createdAt(movie.getCreatedAt())
            .updatedAt(movie.getUpdatedAt())
            .country(movie.getCountry() != null ? toCountryResponse(movie.getCountry()) : null)
            .genres(movie.getGenres() != null ?
                    movie.getGenres().stream().map(this::toGenreResponse).toList() : List.of())
            .directors(movie.getDirectors() != null ?
                    movie.getDirectors().stream().map(this::toPersonResponse).toList() : List.of())
            .actors(movie.getActors() != null ?
                    movie.getActors().stream().map(this::toPersonResponse).toList() : List.of())
            .build();
  }

  @Override
  public void updateMovieFromRequest(MovieUpdateRequest request, Movie movie) {
    Optional.ofNullable(request.getTitle())
            .filter(title -> !title.isBlank())
            .ifPresent(movie::setTitle);
    Optional.ofNullable(request.getDescription()).ifPresent(movie::setDescription);
    Optional.ofNullable(request.getDuration()).ifPresent(movie::setDuration);

    Optional.ofNullable(request.getReleaseDate()).ifPresent(movie::setReleaseDate);
    Optional.ofNullable(request.getPremiereDate()).ifPresent(movie::setPremiereDate);
    Optional.ofNullable(request.getEndDate()).ifPresent(movie::setEndDate);

    Optional.ofNullable(request.getThumbnailUrl()).ifPresent(movie::setThumbnailUrl);
    Optional.ofNullable(request.getTrailerUrl()).ifPresent(movie::setTrailerUrl);

    Optional.ofNullable(request.getMovieStatus()).ifPresent(movie::setMovieStatus);
    Optional.ofNullable(request.getAgeRestriction()).ifPresent(movie::setAgeRestriction);

    Optional.ofNullable(request.getAvailableFormats()).ifPresent(movie::setAvailableFormats);

    Optional.ofNullable(request.getIsDeleted()).ifPresent(movie::setIsDeleted);
    Optional.ofNullable(request.getIsPublished()).ifPresent(movie::setIsPublished);

    if (request.getCountryId() != null) {
      Country country = countryRepository
              .findById(request.getCountryId())
              .orElseThrow(() -> new AppException(ErrorCode.INVALID_COUNTRY));
      movie.setCountry(country);
    }

    if (request.getGenreIds() != null) {
      if (request.getGenreIds().isEmpty()) {
        movie.setGenres(new ArrayList<>());
      } else {
        List<Genre> genres = genreRepository.findAllById(request.getGenreIds());
        if (genres.size() != request.getGenreIds().size()) {
          throw new AppException(ErrorCode.INVALID_GENRE);
        }
        movie.setGenres(genres);
      }
    }

    if (request.getDirectorIds() != null) {
      if (request.getDirectorIds().isEmpty()) {
        movie.setDirectors(new ArrayList<>());
      } else {
        List<Person> directors = personRepository.findAllById(request.getDirectorIds());
        if (directors.size() != request.getDirectorIds().size()) {
          throw new AppException(ErrorCode.INVALID_PERSON);
        }
        movie.setDirectors(directors);
      }
    }

    if (request.getActorIds() != null) {
      if (request.getActorIds().isEmpty()) {
        movie.setActors(new ArrayList<>());
      } else {
        List<Person> actors = personRepository.findAllById(request.getActorIds());
        if (actors.size() != request.getActorIds().size()) {
          throw new AppException(ErrorCode.INVALID_PERSON);
        }
        movie.setActors(actors);
      }
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
