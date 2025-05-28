package com.sba301.online_ticket_sales.controller;

import com.sba301.online_ticket_sales.dto.common.ApiResponseDTO;
import com.sba301.online_ticket_sales.dto.person.request.PersonCreationRequest;
import com.sba301.online_ticket_sales.dto.person.request.PersonUpdateRequest;
import com.sba301.online_ticket_sales.dto.person.response.PersonResponse;
import com.sba301.online_ticket_sales.enums.Occupation;
import com.sba301.online_ticket_sales.service.PersonService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Person Controller", description = "APIs quản lý thông tin Person (diễn viên, đạo diễn)")
public class PersonController {
    PersonService personService;

    @Operation(
            summary = "Tạo mới Person",
            description = "Tạo một Person mới (diễn viên hoặc đạo diễn) với thông tin được cung cấp. Yêu cầu quyền ADMIN hoặc MANAGER (hiện bị vô hiệu hóa)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tạo mới thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Country không tồn tại",
                    content = @Content)
    })
    @PostMapping
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponseDTO<PersonResponse>> createPerson(
            @Valid @RequestBody PersonCreationRequest request) {
        PersonResponse response = personService.createPerson(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.<PersonResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Tạo mới thành công")
                        .result(response)
                        .build());
    }

    @Operation(
            summary = "Cập nhật Person",
            description = "Cập nhật thông tin của Person theo ID. Chỉ cập nhật các trường được cung cấp. Yêu cầu quyền ADMIN hoặc MANAGER (hiện bị vô hiệu hóa)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Person hoặc Country không tồn tại",
                    content = @Content)
    })
    @PutMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponseDTO<PersonResponse>> updatePerson(
            @Parameter(description = "ID của Person cần cập nhật", required = true)
            @PathVariable Integer id,
            @Valid @RequestBody PersonUpdateRequest request) {
        PersonResponse response = personService.updatePerson(id, request);
        return ResponseEntity.ok(ApiResponseDTO.<PersonResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Cập nhật thành công")
                .result(response)
                .build());
    }

    @Operation(
            summary = "Xóa Person (xóa mềm)",
            description = "Xóa mềm Person theo ID bằng cách đặt isDeleted = true, xóa liên kết với Movie và Country. Yêu cầu quyền ADMIN hoặc MANAGER (hiện bị vô hiệu hóa)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xóa thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Person không tồn tại hoặc đã bị xóa",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponseDTO<Void>> deletePerson(
            @Parameter(description = "ID của Person cần xóa", required = true)
            @PathVariable Integer id) {
        personService.deletePerson(id);
        return ResponseEntity.ok(ApiResponseDTO.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Xóa thành công")
                .build());
    }

    @Operation(
            summary = "Lấy chi tiết Person",
            description = "Lấy thông tin chi tiết của Person theo ID. Yêu cầu quyền ADMIN, MANAGER hoặc CUSTOMER (hiện bị vô hiệu hóa)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy chi tiết thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Person không tồn tại hoặc đã bị xóa",
                    content = @Content)
    })
    @GetMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<ApiResponseDTO<PersonResponse>> getPersonDetail(
            @Parameter(description = "ID của Person cần lấy", required = true)
            @PathVariable Integer id) {
        PersonResponse response = personService.getPersonDetail(id);
        return ResponseEntity.ok(ApiResponseDTO.<PersonResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy chi tiết thành công")
                .result(response)
                .build());
    }

    @Operation(
            summary = "Lấy danh sách Persons",
            description = "Lấy danh sách Persons với phân trang, tìm kiếm theo tên, lọc theo nghề nghiệp, và sắp xếp theo tên hoặc ngày sinh. Yêu cầu quyền ADMIN, MANAGER hoặc CUSTOMER (hiện bị vô hiệu hóa)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    @GetMapping
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<ApiResponseDTO<Page<PersonResponse>>> getAllPersons(
            @Parameter(description = "Số trang (bắt đầu từ 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số bản ghi mỗi trang", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Từ khóa tìm kiếm trên tên", example = "Nguyen")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "Nghề nghiệp để lọc (ACTOR hoặc DIRECTOR)", example = "ACTOR")
            @RequestParam(required = false) Occupation occupation,
            @Parameter(description = "Trường để sắp xếp (name, birthDate)", example = "name")
            @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Hướng sắp xếp (asc hoặc desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(
                Sort.Direction.fromString(sortDir), sortBy));
        Page<PersonResponse> response = personService.getAllPersons(pageable, keyword, occupation);
        return ResponseEntity.ok(ApiResponseDTO.<Page<PersonResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy danh sách thành công")
                .result(response)
                .build());
    }
}