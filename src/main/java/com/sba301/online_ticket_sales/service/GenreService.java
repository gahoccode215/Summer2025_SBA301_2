package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.genre.request.GenreCreationRequest;
import com.sba301.online_ticket_sales.dto.genre.response.GenreResponse;

public interface GenreService {
    GenreResponse createGenre(GenreCreationRequest request);
}
