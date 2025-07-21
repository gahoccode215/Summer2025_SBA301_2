package com.sba301.online_ticket_sales.controller;

import com.sba301.online_ticket_sales.dto.common.ApiResponseDTO;
import com.sba301.online_ticket_sales.dto.dashboard.DashboardResponse;
import com.sba301.online_ticket_sales.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
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
@Tag(name = "Dashboard Controller")
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

  DashboardService dashboardService;

  @Operation(
      summary = "Lấy dashboard",
      description =
          "Lấy thông tin dashboard. Admin xem tất cả hệ thống, Manager xem cinema mình quản lý. Mặc định lấy dữ liệu 30 ngày gần nhất nếu không truyền thời gian.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy dashboard thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Chưa đăng nhập hoặc token không hợp lệ",
        content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Không có quyền truy cập dashboard",
        content = @Content)
  })
  @GetMapping
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public ResponseEntity<ApiResponseDTO<DashboardResponse>> getDashboard(
      @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate) {

    log.info("Getting dashboard with startDate: {}, endDate: {}", startDate, endDate);

    // Default to last 30 days if not specified
    if (startDate == null) {
      startDate = LocalDate.now().minusDays(30);
    }
    if (endDate == null) {
      endDate = LocalDate.now();
    }

    // Validate date range
    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("Ngày bắt đầu không thể sau ngày kết thúc");
    }

    DashboardResponse dashboard = dashboardService.getDashboard(startDate, endDate);

    log.info("Dashboard retrieved successfully for period {} to {}", startDate, endDate);

    return ResponseEntity.ok(
        ApiResponseDTO.<DashboardResponse>builder()
            .code(HttpStatus.OK.value())
            .message("Lấy dashboard thành công")
            .result(dashboard)
            .build());
  }

  @Operation(
      summary = "Lấy dashboard admin",
      description = "Lấy dashboard tổng quan toàn hệ thống. Chỉ Admin mới có quyền truy cập.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy dashboard admin thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Chưa đăng nhập hoặc token không hợp lệ",
        content = @Content),
    @ApiResponse(responseCode = "403", description = "Không có quyền admin", content = @Content)
  })
  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponseDTO<DashboardResponse>> getAdminDashboard(
      @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate) {

    log.info("Getting admin dashboard with startDate: {}, endDate: {}", startDate, endDate);

    // Default to last 30 days if not specified
    if (startDate == null) {
      startDate = LocalDate.now().minusDays(30);
    }
    if (endDate == null) {
      endDate = LocalDate.now();
    }

    // Validate date range
    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("Ngày bắt đầu không thể sau ngày kết thúc");
    }

    DashboardResponse dashboard = dashboardService.getAdminDashboard(startDate, endDate);

    log.info("Admin dashboard retrieved successfully for period {} to {}", startDate, endDate);

    return ResponseEntity.ok(
        ApiResponseDTO.<DashboardResponse>builder()
            .code(HttpStatus.OK.value())
            .message("Lấy dashboard admin thành công")
            .result(dashboard)
            .build());
  }

  @Operation(
      summary = "Lấy dashboard cinema",
      description =
          "Lấy dashboard của một cinema cụ thể. Admin có thể xem tất cả cinema, Manager chỉ xem cinema mình quản lý.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy dashboard cinema thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Chưa đăng nhập hoặc token không hợp lệ",
        content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Không có quyền xem cinema này",
        content = @Content),
    @ApiResponse(responseCode = "404", description = "Cinema không tồn tại", content = @Content)
  })
  @GetMapping("/cinema/{cinemaId}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public ResponseEntity<ApiResponseDTO<DashboardResponse>> getCinemaDashboard(
      @Parameter(description = "ID của cinema") @PathVariable Long cinemaId,
      @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate) {

    log.info(
        "Getting cinema dashboard for cinema: {} with startDate: {}, endDate: {}",
        cinemaId,
        startDate,
        endDate);

    // Default to last 30 days if not specified
    if (startDate == null) {
      startDate = LocalDate.now().minusDays(30);
    }
    if (endDate == null) {
      endDate = LocalDate.now();
    }

    // Validate date range
    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("Ngày bắt đầu không thể sau ngày kết thúc");
    }

    DashboardResponse dashboard =
        dashboardService.getManagerDashboard(cinemaId, startDate, endDate);

    log.info(
        "Cinema dashboard retrieved successfully for cinema: {} and period {} to {}",
        cinemaId,
        startDate,
        endDate);

    return ResponseEntity.ok(
        ApiResponseDTO.<DashboardResponse>builder()
            .code(HttpStatus.OK.value())
            .message("Lấy dashboard cinema thành công")
            .result(dashboard)
            .build());
  }

  @Operation(
      summary = "Lấy dashboard nhanh",
      description =
          "Lấy dashboard với khoảng thời gian định sẵn: today (hôm nay), week (tuần qua), month (tháng qua), quarter (quý qua), year (năm qua).")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy dashboard nhanh thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Khoảng thời gian không hợp lệ",
        content = @Content),
    @ApiResponse(
        responseCode = "401",
        description = "Chưa đăng nhập hoặc token không hợp lệ",
        content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Không có quyền truy cập dashboard",
        content = @Content)
  })
  @GetMapping("/quick/{period}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public ResponseEntity<ApiResponseDTO<DashboardResponse>> getDashboardQuick(
      @Parameter(description = "Khoảng thời gian: today, week, month, quarter, year") @PathVariable
          String period) {

    log.info("Getting dashboard for quick period: {}", period);

    LocalDate startDate;
    LocalDate endDate = LocalDate.now();

    switch (period.toLowerCase()) {
      case "today":
        startDate = LocalDate.now();
        break;
      case "week":
        startDate = LocalDate.now().minusWeeks(1);
        break;
      case "month":
        startDate = LocalDate.now().minusMonths(1);
        break;
      case "quarter":
        startDate = LocalDate.now().minusMonths(3);
        break;
      case "year":
        startDate = LocalDate.now().minusYears(1);
        break;
      default:
        throw new IllegalArgumentException(
            "Khoảng thời gian không hợp lệ. Sử dụng: today, week, month, quarter, year");
    }

    DashboardResponse dashboard = dashboardService.getDashboard(startDate, endDate);

    log.info("Quick dashboard retrieved successfully for period: {}", period);

    return ResponseEntity.ok(
        ApiResponseDTO.<DashboardResponse>builder()
            .code(HttpStatus.OK.value())
            .message("Lấy dashboard nhanh thành công")
            .result(dashboard)
            .build());
  }

  @Operation(
      summary = "Lấy tóm tắt dashboard",
      description =
          "Lấy thông tin tóm tắt dashboard (chỉ overview data, không có biểu đồ và bảng chi tiết).")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy tóm tắt dashboard thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Chưa đăng nhập hoặc token không hợp lệ",
        content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Không có quyền truy cập dashboard",
        content = @Content)
  })
  @GetMapping("/summary")
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public ResponseEntity<ApiResponseDTO<DashboardResponse>> getDashboardSummary(
      @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate) {

    log.info("Getting dashboard summary with startDate: {}, endDate: {}", startDate, endDate);

    // Default to last 30 days if not specified
    if (startDate == null) {
      startDate = LocalDate.now().minusDays(30);
    }
    if (endDate == null) {
      endDate = LocalDate.now();
    }

    // Validate date range
    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("Ngày bắt đầu không thể sau ngày kết thúc");
    }

    // Get full dashboard but only return overview
    DashboardResponse fullDashboard = dashboardService.getDashboard(startDate, endDate);

    // Create summary response with only overview data
    DashboardResponse summaryDashboard =
        DashboardResponse.builder().overview(fullDashboard.getOverview()).build();

    log.info("Dashboard summary retrieved successfully for period {} to {}", startDate, endDate);

    return ResponseEntity.ok(
        ApiResponseDTO.<DashboardResponse>builder()
            .code(HttpStatus.OK.value())
            .message("Lấy tóm tắt dashboard thành công")
            .result(summaryDashboard)
            .build());
  }
}
