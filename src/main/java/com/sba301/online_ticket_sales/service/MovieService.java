package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.request.MovieCreationRequest;

public interface MovieService {
    void createMovie(MovieCreationRequest request);
}
