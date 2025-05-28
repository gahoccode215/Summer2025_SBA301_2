package com.sba301.online_ticket_sales.controller;

import com.sba301.online_ticket_sales.dto.common.ApiResponseDTO;
import com.sba301.online_ticket_sales.dto.genre.request.GenreCreationRequest;
import com.sba301.online_ticket_sales.dto.genre.request.GenreUpdateRequest;
import com.sba301.online_ticket_sales.dto.genre.response.GenreResponse;
import com.sba301.online_ticket_sales.service.GenreService;
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
@Tag(name = "Genre Controller", description = "APIs quản lý thể loại phim")
@RequestMapping("/api/v1/genres")
public class GenreController {
    GenreService genreService;

    @Operation(
            summary = "Tạo mới thể loại phim",
            description = "Tạo một thể loại phim mới với tên được cung cấp. Yêu cầu quyền ADMIN hoặc MANAGER"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tạo mới thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ hoặc tên thể loại đã tồn tại",
                    content = @Content)
    })
    @PostMapping
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponseDTO<GenreResponse>> createGenre(
            @Valid @RequestBody GenreCreationRequest request) {
        GenreResponse response = genreService.createGenre(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.<GenreResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Tạo mới thành công")
                        .result(response)
                        .build());
    }
    @Operation(
            summary = "Cập nhật thể loại phim",
            description = "Cập nhật tên của thể loại phim theo ID. Yêu cầu quyền ADMIN hoặc MANAGER."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ hoặc tên thể loại đã tồn tại",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Thể loại không tồn tại",
                    content = @Content)
    })
    @PutMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponseDTO<GenreResponse>> updateGenre(
            @Parameter(description = "ID của thể loại cần cập nhật", required = true)
            @PathVariable Integer id,
            @Valid @RequestBody GenreUpdateRequest request) {
        GenreResponse response = genreService.updateGenre(id, request);
        return ResponseEntity.ok(ApiResponseDTO.<GenreResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Cập nhật thành công")
                .result(response)
                .build());
    }
}
