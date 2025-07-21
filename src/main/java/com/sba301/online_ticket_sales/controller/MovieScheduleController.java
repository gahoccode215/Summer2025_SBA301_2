package com.sba301.online_ticket_sales.controller;

import com.sba301.online_ticket_sales.dto.common.ApiResponseDTO;
import com.sba301.online_ticket_sales.dto.moviescreen.request.UpsertMovieScreenRequest;
import com.sba301.online_ticket_sales.dto.moviescreen.response.CinemaShowTimeResponse;
import com.sba301.online_ticket_sales.dto.moviescreen.response.MovieShowTimeResponse;
import com.sba301.online_ticket_sales.enums.MovieScreenStatus;
import com.sba301.online_ticket_sales.service.MovieScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Movie Schedule Controller", description = "APIs Manage Movie Schedules")
@RequestMapping("/api/v1/schedules")
public class MovieScheduleController {

  MovieScheduleService movieScheduleService;

  @Operation(
      summary = "Create a new movie schedule",
      description = "Create a new movie schedule with the provided information")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Successfully created movie schedule",
        content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input data or missing required fields",
        content = @Content)
  })
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @PostMapping
  public ResponseEntity<ApiResponseDTO<Void>> createMovieSchedule(
      @RequestBody @Valid UpsertMovieScreenRequest request) {
    movieScheduleService.createMovieSchedule(request);
    return ResponseEntity.ok(
        ApiResponseDTO.<Void>builder()
            .code(HttpStatus.OK.value())
            .message("Movie schedule created successfully")
            .build());
  }

  @Operation(
      summary = "Update movie schedule",
      description = "Update movie schedule with the provided information")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Successfully Updated movie schedule",
        content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input data or missing required fields",
        content = @Content)
  })
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @PutMapping("/{id}")
  public ResponseEntity<ApiResponseDTO<Void>> updateMovieSchedule(
      @RequestBody @Valid UpsertMovieScreenRequest request, @RequestParam Long id) {
    movieScheduleService.updateMovieSchedule(id, request);
    return ResponseEntity.ok(
        ApiResponseDTO.<Void>builder()
            .code(HttpStatus.OK.value())
            .message("Movie schedule updated successfully")
            .build());
  }

  @Operation(
      summary = "Activate or Deactivate movie schedule",
      description = "Activate or Deactivate movie schedule with the provided information")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successfully updated movie schedule status",
        content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input data or missing required fields",
        content = @Content)
  })
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @PutMapping("/{id}/status")
  public ResponseEntity<ApiResponseDTO<Void>> activateMovieSchedule(
      @PathVariable Long id, @RequestParam String status) {
    movieScheduleService.activateMovieSchedule(id, MovieScreenStatus.valueOf(status));
    return ResponseEntity.ok(
        ApiResponseDTO.<Void>builder()
            .code(HttpStatus.OK.value())
            .message("Movie schedule status updated successfully")
            .build());
  }

  @Operation(
      summary = "Get movie showtimes by movie ID and date",
      description = "Retrieve movie showtimes for a specific movie on a given date")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved movie showtimes",
        content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input data or missing required fields",
        content = @Content)
  })
  @GetMapping("/movies/{movieId}")
  public ResponseEntity<ApiResponseDTO<List<CinemaShowTimeResponse>>> getMovieShowTimes(
      @PathVariable Long movieId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime date) {
    var showTimes = movieScheduleService.getMovieShowTimes(movieId, date);
    return ResponseEntity.ok(
        ApiResponseDTO.<List<CinemaShowTimeResponse>>builder()
            .code(HttpStatus.OK.value())
            .message("Successfully retrieved movie showtimes")
            .result(showTimes)
            .build());
  }

  @Operation(
      summary = "Get movie showtimes by cinema ID and date",
      description = "Retrieve movie showtimes for a specific cinema on a given date")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved movie showtimes",
        content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input data or missing required fields",
        content = @Content)
  })
  @GetMapping("/cinemas/{cinemaId}")
  public ResponseEntity<ApiResponseDTO<List<MovieShowTimeResponse>>> getMovieShowTimesByCinema(
      @PathVariable Long cinemaId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime date) {
    var showTimes = movieScheduleService.getMovieShowTimesByCinema(cinemaId, date);
    return ResponseEntity.ok(
        ApiResponseDTO.<List<MovieShowTimeResponse>>builder()
            .code(HttpStatus.OK.value())
            .message("Successfully retrieved movie showtimes by cinema")
            .result(showTimes)
            .build());
  }
}
