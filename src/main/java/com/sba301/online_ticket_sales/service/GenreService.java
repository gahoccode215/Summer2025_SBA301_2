package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.genre.request.GenreCreationRequest;
import com.sba301.online_ticket_sales.dto.genre.request.GenreUpdateRequest;
import com.sba301.online_ticket_sales.dto.genre.response.GenreResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GenreService {
    GenreResponse createGenre(GenreCreationRequest request);
    GenreResponse updateGenre(Integer id, GenreUpdateRequest request);
    void deleteGenre(Integer id);
    GenreResponse getGenreDetail(Integer id);
    Page<GenreResponse> getAllGenres(Pageable pageable, String keyword);
}
