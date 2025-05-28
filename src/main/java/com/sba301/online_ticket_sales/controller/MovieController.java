package com.sba301.online_ticket_sales.controller;

import com.sba301.online_ticket_sales.dto.common.ApiResponseDTO;
import com.sba301.online_ticket_sales.dto.movie.request.MovieCreationRequest;
import com.sba301.online_ticket_sales.service.MovieService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Movie Controller")
@RequestMapping("/api/v1/movies")
public class MovieController {
    MovieService movieService;

    @PostMapping()
    public ResponseEntity<ApiResponseDTO<Void>> createMovie(@RequestBody MovieCreationRequest request) {
        movieService.createMovie(request);
        return ResponseEntity.ok(ApiResponseDTO.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .message("Tạo mới phim thành công")
                .build());
    }
}
