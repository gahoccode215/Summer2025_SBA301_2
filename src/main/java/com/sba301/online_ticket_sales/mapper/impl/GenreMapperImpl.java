package com.sba301.online_ticket_sales.mapper.impl;

import com.sba301.online_ticket_sales.dto.genre.request.GenreCreationRequest;
import com.sba301.online_ticket_sales.dto.genre.request.GenreUpdateRequest;
import com.sba301.online_ticket_sales.dto.genre.response.GenreResponse;
import com.sba301.online_ticket_sales.entity.Genre;
import com.sba301.online_ticket_sales.mapper.GenreMapper;
import org.springframework.stereotype.Component;

@Component
public class GenreMapperImpl implements GenreMapper {

  @Override
  public Genre toGenre(GenreCreationRequest request) {
    return Genre.builder().name(request.getName()).build();
  }

  @Override
  public GenreResponse toGenreResponse(Genre genre) {
    GenreResponse response = new GenreResponse();
    response.setId(genre.getId());
    response.setName(genre.getName());
    return response;
  }

  @Override
  public void updateGenreFromRequest(GenreUpdateRequest request, Genre genre) {
    genre.setName(request.getName());
  }
}
