package com.sba301.online_ticket_sales.controller;

import com.sba301.online_ticket_sales.dto.common.ApiResponseDTO;
import com.sba301.online_ticket_sales.dto.movie.request.MovieCreationRequest;
import com.sba301.online_ticket_sales.dto.movie.request.MovieUpdateRequest;
import com.sba301.online_ticket_sales.dto.movie.response.MovieResponse;
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
}