package com.sba301.online_ticket_sales.mapper;

import com.sba301.online_ticket_sales.dto.genre.request.GenreCreationRequest;
import com.sba301.online_ticket_sales.dto.genre.request.GenreUpdateRequest;
import com.sba301.online_ticket_sales.dto.genre.response.GenreResponse;
import com.sba301.online_ticket_sales.entity.Genre;

public interface GenreMapper {
    Genre toGenre(GenreCreationRequest request);
    GenreResponse toGenreResponse(Genre genre);
    void updateGenreFromRequest(GenreUpdateRequest request, Genre genre);
}
