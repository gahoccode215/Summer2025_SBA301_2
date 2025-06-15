package com.sba301.online_ticket_sales.controller;

import com.sba301.online_ticket_sales.dto.cinema.request.CinemaRequest;
import com.sba301.online_ticket_sales.dto.cinema.response.CinemaDetailResponse;
import com.sba301.online_ticket_sales.dto.cinema.response.CinemaResponse;
import com.sba301.online_ticket_sales.dto.common.ApiResponseDTO;
import com.sba301.online_ticket_sales.service.CinemaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  @Operation(
      summary = "Upsert Cinema",
      description =
          "Upsert a cinema with the provided details. If the cinema does not exist, it will be created. If it exists, it will be updated.")
  public ResponseEntity<ApiResponseDTO<Long>> upsertCinema(
      @Valid @RequestBody CinemaRequest request) {

    Long cinemaId = cinemaService.upsertCinema(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            ApiResponseDTO.<Long>builder()
                .code(HttpStatus.CREATED.value())
                .message(request.getRequestType() + " thành công")
                .result(cinemaId)
                .build());
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STAFF')")
  @Operation(
      summary = "Get All Cinemas",
      description =
          "Retrieve a list of all cinemas. Accessible to ADMIN, MANAGER, and STAFF roles.")
  public ResponseEntity<ApiResponseDTO<List<CinemaResponse>>> getAllCinema() {
    List<CinemaResponse> result = cinemaService.getAllCinemasWithAuthentication();
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            ApiResponseDTO.<List<CinemaResponse>>builder()
                .code(HttpStatus.CREATED.value())
                .message("Lấy danh sách rạp chiếu thành công " + result.size() + " rạp")
                .result(result)
                .build());
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STAFF')")
  @Operation(
      summary = "Get Cinema Detail by ID",
      description =
          "Retrieve the details of a cinema by its ID. Accessible to ADMIN, MANAGER, and STAFF roles.")
  public ResponseEntity<ApiResponseDTO<CinemaDetailResponse>> getCinemaById(@PathVariable Long id) {
    CinemaDetailResponse result = cinemaService.getCinemaDetail(id);
    return ResponseEntity.ok(
        ApiResponseDTO.<CinemaDetailResponse>builder()
            .code(HttpStatus.OK.value())
            .message("Lấy chi tiết rạp chiếu thành công " + result.getId())
            .result(result)
            .build());
  }

  @PutMapping("/{id}/active")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(
      summary = "Activate/Deactivate Cinema",
      description = "Activate or deactivate a cinema by its ID. Only accessible to ADMIN role.")
  public ResponseEntity<ApiResponseDTO<Void>> deActivateCinema(
      @PathVariable Long id, @RequestParam boolean active) {
    cinemaService.deActivate(id, active);
    return ResponseEntity.ok(
        ApiResponseDTO.<Void>builder()
            .code(HttpStatus.OK.value())
            .message("Cập nhật trạng thái rạp chiếu thành công " + id + ", active: " + active)
            .build());
  }

  @GetMapping("/customer")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Get All Cinemas for Customer",
      description = "Retrieve a list of all cinemas available for customers.")
  public ResponseEntity<ApiResponseDTO<List<CinemaResponse>>> getAllCinemasForCustomer() {
    List<CinemaResponse> result = cinemaService.getAllCinemasForCustomer();
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            ApiResponseDTO.<List<CinemaResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy danh sách rạp chiếu thành công " + result.size() + " rạp")
                .result(result)
                .build());
  }
}
