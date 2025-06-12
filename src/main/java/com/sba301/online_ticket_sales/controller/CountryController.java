package com.sba301.online_ticket_sales.controller;

import com.sba301.online_ticket_sales.dto.common.ApiResponseDTO;
import com.sba301.online_ticket_sales.dto.country.request.CountryCreationRequest;
import com.sba301.online_ticket_sales.dto.country.request.CountryUpdateRequest;
import com.sba301.online_ticket_sales.dto.country.response.CountryResponse;
import com.sba301.online_ticket_sales.service.CountryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Country Controller", description = "APIs quản lý quốc gia")
@RequestMapping("/api/v1/countries")
public class CountryController {
  CountryService countryService;

  @Operation(
      summary = "Tạo mới quốc gia",
      description = "Tạo một quốc gia mới với tên được cung cấp. Yêu cầu quyền ADMIN hoặc MANAGER.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Tạo mới thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Dữ liệu đầu vào không hợp lệ hoặc tên quốc gia đã tồn tại",
        content = @Content)
  })
  @PostMapping
   @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public ResponseEntity<ApiResponseDTO<CountryResponse>> createCountry(
      @Valid @RequestBody CountryCreationRequest request) {
    CountryResponse response = countryService.createCountry(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            ApiResponseDTO.<CountryResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Tạo mới thành công")
                .result(response)
                .build());
  }

  @Operation(
      summary = "Cập nhật quốc gia",
      description = "Cập nhật tên quốc gia theo ID. Yêu cầu quyền ADMIN hoặc MANAGER.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Cập nhật thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Dữ liệu đầu vào không hợp lệ hoặc tên quốc gia đã tồn tại",
        content = @Content),
    @ApiResponse(responseCode = "404", description = "Quốc gia không tồn tại", content = @Content)
  })
  @PutMapping("/{id}")
   @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public ResponseEntity<ApiResponseDTO<CountryResponse>> updateCountry(
      @Parameter(description = "ID của quốc gia cần cập nhật", required = true) @PathVariable
          Integer id,
      @Valid @RequestBody CountryUpdateRequest request) {
    CountryResponse response = countryService.updateCountry(id, request);
    return ResponseEntity.ok(
        ApiResponseDTO.<CountryResponse>builder()
            .code(HttpStatus.OK.value())
            .message("Cập nhật thành công")
            .result(response)
            .build());
  }

  @Operation(
      summary = "Xóa quốc gia (xóa cứng)",
      description =
          "Xóa quốc gia theo ID, đặt country_id thành NULL trong các bảng liên quan (ví dụ: persons, movies) mà không xóa các thực thể liên kết. Yêu cầu quyền ADMIN hoặc MANAGER.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Xóa thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
    @ApiResponse(responseCode = "404", description = "Quốc gia không tồn tại", content = @Content)
  })
  @DeleteMapping("/{id}")
   @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public ResponseEntity<ApiResponseDTO<Void>> deleteCountry(
      @Parameter(description = "ID của quốc gia cần xóa", required = true) @PathVariable
          Integer id) {
    countryService.deleteCountry(id);
    return ResponseEntity.ok(
        ApiResponseDTO.<Void>builder()
            .code(HttpStatus.OK.value())
            .message("Xóa thành công")
            .build());
  }

  @Operation(
      summary = "Lấy chi tiết quốc gia",
      description =
          "Lấy thông tin chi tiết của quốc gia theo ID. Yêu cầu quyền ADMIN, MANAGER hoặc CUSTOMER.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy chi tiết thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
    @ApiResponse(responseCode = "404", description = "Quốc gia không tồn tại", content = @Content)
  })
  @GetMapping("/{id}")
   @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CUSTOMER')")
  public ResponseEntity<ApiResponseDTO<CountryResponse>> getCountryDetail(
      @Parameter(description = "ID của quốc gia cần lấy", required = true) @PathVariable
          Integer id) {
    CountryResponse response = countryService.getCountryDetail(id);
    return ResponseEntity.ok(
        ApiResponseDTO.<CountryResponse>builder()
            .code(HttpStatus.OK.value())
            .message("Lấy chi tiết thành công")
            .result(response)
            .build());
  }

  @Operation(
      summary = "Lấy danh sách quốc gia",
      description =
          "Lấy danh sách quốc gia với phân trang, tìm kiếm theo tên, và sắp xếp theo tên. Yêu cầu quyền ADMIN, MANAGER hoặc CUSTOMER.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy danh sách thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDTO.class)))
  })
  @GetMapping
   @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CUSTOMER')")
  public ResponseEntity<ApiResponseDTO<Page<CountryResponse>>> getAllCountries(
      @Parameter(description = "Số trang (bắt đầu từ 0)", example = "0")
          @RequestParam(defaultValue = "0")
          int page,
      @Parameter(description = "Số bản ghi mỗi trang", example = "10")
          @RequestParam(defaultValue = "10")
          int size,
      @Parameter(description = "Từ khóa tìm kiếm trên tên", example = "Việt")
          @RequestParam(required = false)
          String keyword,
      @Parameter(description = "Trường để sắp xếp (hiện chỉ hỗ trợ name)", example = "name")
          @RequestParam(defaultValue = "name")
          String sortBy,
      @Parameter(description = "Hướng sắp xếp (asc hoặc desc)", example = "asc")
          @RequestParam(defaultValue = "asc")
          String sortDir) {
    Pageable pageable =
        PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
    Page<CountryResponse> response = countryService.getAllCountries(pageable, keyword);
    return ResponseEntity.ok(
        ApiResponseDTO.<Page<CountryResponse>>builder()
            .code(HttpStatus.OK.value())
            .message("Lấy danh sách thành công")
            .result(response)
            .build());
  }
}
