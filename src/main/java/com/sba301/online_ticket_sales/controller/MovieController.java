package com.sba301.online_ticket_sales.controller;

import com.sba301.online_ticket_sales.dto.common.ApiResponseDTO;
import com.sba301.online_ticket_sales.dto.movie.request.MovieCreationRequest;
import com.sba301.online_ticket_sales.dto.movie.request.MovieUpdateRequest;
import com.sba301.online_ticket_sales.dto.movie.response.MovieResponse;
import com.sba301.online_ticket_sales.enums.MovieStatus;
import com.sba301.online_ticket_sales.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Movie Controller", description = "APIs quản lý phim")
@RequestMapping("/api/v1/movies")
public class MovieController {
    MovieService movieService;

    @Operation(
            summary = "Tạo mới phim",
            description = "Tạo một phim mới với thông tin được cung cấp. Yêu cầu quyền ADMIN hoặc MANAGER."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tạo mới thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ hoặc ID liên kết không tồn tại",
                    content = @Content)
    })
    @PostMapping
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponseDTO<MovieResponse>> createMovie(
            @Valid @RequestBody MovieCreationRequest request) {
        MovieResponse response = movieService.createMovie(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.<MovieResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Tạo mới thành công")
                        .result(response)
                        .build());
    }
    @Operation(
            summary = "Cập nhật phim",
            description = "Cập nhật thông tin phim theo ID, chỉ cập nhật các trường được cung cấp. Yêu cầu quyền ADMIN hoặc MANAGER."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ hoặc ID liên kết không tồn tại",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Phim không tồn tại",
                    content = @Content)
    })
    @PutMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponseDTO<MovieResponse>> updateMovie(
            @Parameter(description = "ID của phim cần cập nhật", required = true)
            @PathVariable Long id,
            @Valid @RequestBody MovieUpdateRequest request) {
        MovieResponse response = movieService.updateMovie(id, request);
        return ResponseEntity.ok(ApiResponseDTO.<MovieResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Cập nhật thành công")
                .result(response)
                .build());
    }
    @Operation(
            summary = "Xóa phim (xóa mềm)",
            description = "Xóa mềm phim theo ID bằng cách đặt isDeleted = true, xóa liên kết với Country, Genre, Person (directors, actors) mà không xóa các thực thể liên kết. Yêu cầu quyền ADMIN hoặc MANAGER."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xóa thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Phim không tồn tại hoặc đã bị xóa",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponseDTO<Void>> deleteMovie(
            @Parameter(description = "ID của phim cần xóa", required = true)
            @PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.ok(ApiResponseDTO.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Xóa thành công")
                .build());
    }
    @Operation(
            summary = "Lấy chi tiết phim",
            description = "Lấy thông tin chi tiết của phim theo ID, chỉ trả về phim chưa bị xóa mềm (isDeleted = false). Yêu cầu quyền ADMIN, MANAGER hoặc CUSTOMER ."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy chi tiết thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Phim không tồn tại hoặc đã bị xóa",
                    content = @Content)
    })
    @GetMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<ApiResponseDTO<MovieResponse>> getMovieDetail(
            @Parameter(description = "ID của phim cần lấy", required = true)
            @PathVariable Long id) {
        MovieResponse response = movieService.getMovieDetail(id);
        return ResponseEntity.ok(ApiResponseDTO.<MovieResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy chi tiết thành công")
                .result(response)
                .build());
    }
    @Operation(
            summary = "Lấy danh sách phim",
            description = "Lấy danh sách phim với phân trang, tìm kiếm theo tiêu đề, sắp xếp theo tiêu đề, thời lượng hoặc ngày phát hành, và lọc theo trạng thái phim (UPCOMING, NOW_SHOWING, ENDED, IMAX). Chỉ trả về phim chưa bị xóa mềm (isDeleted = false). Yêu cầu quyền ADMIN, MANAGER hoặc CUSTOMER (hiện bị vô hiệu hóa)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    @GetMapping
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<ApiResponseDTO<Page<MovieResponse>>> getAllMovies(
            @Parameter(description = "Số trang (bắt đầu từ 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số bản ghi mỗi trang", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Từ khóa tìm kiếm trên tiêu đề", example = "Avengers")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "Trạng thái phim để lọc (UPCOMING, NOW_SHOWING, ENDED, IMAX)", example = "NOW_SHOWING")
            @RequestParam(required = false) MovieStatus movieStatus,
            @Parameter(description = "Trường để sắp xếp (title, duration, releaseDate)", example = "title")
            @RequestParam(defaultValue = "title") String sortBy,
            @Parameter(description = "Hướng sắp xếp (asc hoặc desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(
                Sort.Direction.fromString(sortDir), sortBy));
        Page<MovieResponse> response = movieService.getAllMovies(pageable, keyword, movieStatus);
        return ResponseEntity.ok(ApiResponseDTO.<Page<MovieResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy danh sách thành công")
                .result(response)
                .build());
    }
}