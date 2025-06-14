package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.genre.request.GenreCreationRequest;
import com.sba301.online_ticket_sales.dto.genre.request.GenreUpdateRequest;
import com.sba301.online_ticket_sales.dto.genre.response.GenreResponse;
import com.sba301.online_ticket_sales.entity.Genre;
import com.sba301.online_ticket_sales.entity.Movie;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.mapper.GenreMapper;
import com.sba301.online_ticket_sales.repository.GenreRepository;
import com.sba301.online_ticket_sales.repository.MovieRepository;
import com.sba301.online_ticket_sales.service.GenreService;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreServiceImpl implements GenreService {

  GenreRepository genreRepository;
  GenreMapper genreMapper;

  @Override
  @Transactional
  public GenreResponse createGenre(GenreCreationRequest request) {
    try {
      Genre genre = genreMapper.toGenre(request);
      Genre savedGenre = genreRepository.save(genre);
      return genreMapper.toGenreResponse(savedGenre);
    } catch (DataIntegrityViolationException e) {
      throw new AppException(ErrorCode.GENRE_ALREADY_EXISTS);
    }
  }

  @Override
  @Transactional
  public GenreResponse updateGenre(Integer id, GenreUpdateRequest request) {
    Genre genre =
        genreRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_FOUND));
    try {
      genreMapper.updateGenreFromRequest(request, genre);
      Genre updatedGenre = genreRepository.save(genre);
      return genreMapper.toGenreResponse(updatedGenre);
    } catch (DataIntegrityViolationException e) {
      throw new AppException(ErrorCode.GENRE_ALREADY_EXISTS);
    }
  }

  @Override
  @Transactional
  public void deleteGenre(Integer id) {

    Genre genre = genreRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_FOUND));

    if (genreRepository.isGenreInUse(id)) {
      throw new AppException(ErrorCode.GENRE_IN_USE);
    }

    genreRepository.delete(genre);
  }

  @Override
  public GenreResponse getGenreDetail(Integer id) {
    Genre genre =
        genreRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_FOUND));
    return genreMapper.toGenreResponse(genre);
  }

  @Override
  public Page<GenreResponse> getAllGenres(Pageable pageable, String keyword) {
    Specification<Genre> spec =
        (root, query, cb) -> {
          List<Predicate> predicates = new ArrayList<>();
          if (keyword != null && !keyword.isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
          }
          return cb.and(predicates.toArray(new Predicate[0]));
        };
    Page<Genre> genres = genreRepository.findAll(spec, pageable);
    return genres.map(genreMapper::toGenreResponse);
  }
}
