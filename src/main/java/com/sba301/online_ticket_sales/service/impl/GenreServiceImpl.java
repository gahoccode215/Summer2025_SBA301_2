package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.genre.request.GenreCreationRequest;
import com.sba301.online_ticket_sales.dto.genre.request.GenreUpdateRequest;
import com.sba301.online_ticket_sales.dto.genre.response.GenreResponse;
import com.sba301.online_ticket_sales.entity.Genre;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.mapper.GenreMapper;
import com.sba301.online_ticket_sales.repository.GenreRepository;
import com.sba301.online_ticket_sales.service.GenreService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreServiceImpl implements GenreService {

    GenreRepository genreRepository;
    GenreMapper genreMapper;

    @Override
    public GenreResponse createGenre(GenreCreationRequest request) {
        try {
            Genre genre = genreMapper.toGenre(request);
            Genre savedGenre = genreRepository.save(genre);
            log.info("Created genre: {}", savedGenre.getName());
            return genreMapper.toGenreResponse(savedGenre);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.GENRE_ALREADY_EXISTS);
        }
    }
    @Override
    public GenreResponse updateGenre(Integer id, GenreUpdateRequest request) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_FOUND));
        try {
            genreMapper.updateGenreFromRequest(request, genre);
            Genre updatedGenre = genreRepository.save(genre);
            log.info("Updated genre: {}", updatedGenre.getName());
            return genreMapper.toGenreResponse(updatedGenre);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.GENRE_ALREADY_EXISTS);
        }
    }
    @Override
    public void deleteGenre(Integer id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_FOUND));
        genreRepository.delete(genre);
        log.info("Deleted genre: {}", genre.getName());
    }
}
