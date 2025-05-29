package com.sba301.online_ticket_sales.controller;

import com.sba301.online_ticket_sales.dto.cinema.request.CinemaRequest;
import com.sba301.online_ticket_sales.dto.cinema.response.CinemaDetailResponse;
import com.sba301.online_ticket_sales.dto.cinema.response.CinemaResponse;
import com.sba301.online_ticket_sales.dto.common.ApiResponse;
import com.sba301.online_ticket_sales.service.CinemaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Cinema Controller")
@RequestMapping("/api/v1/cinemas")
public class CinemaController {
  CinemaService cinemaService;

  @PostMapping
  public ResponseEntity<ApiResponse<Long>> upsertCinema(@Valid @RequestBody CinemaRequest request) {
    Long cinemaId = cinemaService.upsertCinema(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            ApiResponse.<Long>builder()
                .code(HttpStatus.CREATED.value())
                .message(request.getRequestType() + " thành công")
                .result(cinemaId)
                .build());
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<CinemaResponse>>> getAllCinema() {
    List<CinemaResponse> result = cinemaService.getAllCinemas();
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            ApiResponse.<List<CinemaResponse>>builder()
                .code(HttpStatus.CREATED.value())
                .message("Lấy danh sách rạp chiếu thành công " + result.size() + " rạp")
                .result(result)
                .build());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<CinemaDetailResponse>> getCinemaById(@PathVariable Long id) {
    CinemaDetailResponse result = cinemaService.getCinemaDetail(id);
    return ResponseEntity.ok(
        ApiResponse.<CinemaDetailResponse>builder()
            .code(HttpStatus.OK.value())
            .message("Lấy chi tiết rạp chiếu thành công " + result.getId())
            .result(result)
            .build());
  }

  @PutMapping("/{id}/active")
  public ResponseEntity<ApiResponse<Void>> deActivateCinema(
      @PathVariable Long id, @RequestParam boolean active) {
    cinemaService.deActivate(id, active);
    return ResponseEntity.ok(
        ApiResponse.<Void>builder()
            .code(HttpStatus.OK.value())
            .message("Cập nhật trạng thái rạp chiếu thành công " + id + ", active: " + active)
            .build());
  }
}
