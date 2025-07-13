package com.sba301.online_ticket_sales.controller;

import com.sba301.online_ticket_sales.dto.common.ApiResponseDTO;
import com.sba301.online_ticket_sales.dto.person.request.PersonCreationRequest;
import com.sba301.online_ticket_sales.dto.person.request.PersonUpdateRequest;
import com.sba301.online_ticket_sales.dto.person.response.PersonResponse;
import com.sba301.online_ticket_sales.enums.Occupation;
import com.sba301.online_ticket_sales.service.PersonService;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(
    name = "Person Controller",
    description = "APIs quản lý thông tin Person (diễn viên, đạo diễn)")
@RequestMapping("/api/v1/persons")
public class PersonController {
  PersonService personService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public ResponseEntity<ApiResponseDTO<PersonResponse>> createPerson(
      @Valid @RequestPart("person") PersonCreationRequest request,
      @RequestPart(value = "images", required = false) MultipartFile[] imagesFile) {
    PersonResponse response = personService.createPerson(request, imagesFile);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            ApiResponseDTO.<PersonResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Tạo mới thành công")
                .result(response)
                .build());
  }

  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public ResponseEntity<ApiResponseDTO<PersonResponse>> updatePerson(
      @Parameter(description = "ID của Person cần cập nhật", required = true) @PathVariable
          Integer id,
      @Valid @RequestPart("person") PersonUpdateRequest request,
      @RequestPart(value = "thumbnail", required = false) MultipartFile[] imagesFile) {
    PersonResponse response = personService.updatePerson(id, request, imagesFile);
    return ResponseEntity.ok(
        ApiResponseDTO.<PersonResponse>builder()
            .code(HttpStatus.OK.value())
            .message("Cập nhật thành công")
            .result(response)
            .build());
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public ResponseEntity<ApiResponseDTO<Void>> deletePerson(
      @Parameter(description = "ID của Person cần xóa", required = true) @PathVariable Integer id) {
    personService.deletePerson(id);
    return ResponseEntity.ok(
        ApiResponseDTO.<Void>builder()
            .code(HttpStatus.OK.value())
            .message("Xóa thành công")
            .build());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponseDTO<PersonResponse>> getPersonDetail(
      @Parameter(description = "ID của Person cần lấy", required = true) @PathVariable Integer id) {
    PersonResponse response = personService.getPersonDetail(id);
    return ResponseEntity.ok(
        ApiResponseDTO.<PersonResponse>builder()
            .code(HttpStatus.OK.value())
            .message("Lấy chi tiết thành công")
            .result(response)
            .build());
  }

  @GetMapping
  public ResponseEntity<ApiResponseDTO<Page<PersonResponse>>> getAllPersons(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String keyword,
      @Parameter(description = "Nghề nghiệp để lọc (ACTOR hoặc DIRECTOR)", example = "ACTOR")
          @RequestParam(required = false)
          Occupation occupation,
      @Parameter(description = "Trường để sắp xếp (name, birthDate)", example = "name")
          @RequestParam(defaultValue = "name")
          String sortBy,
      @Parameter(description = "Hướng sắp xếp (asc hoặc desc)", example = "asc")
          @RequestParam(defaultValue = "asc")
          String sortDir) {
    Pageable pageable =
        PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
    Page<PersonResponse> response = personService.getAllPersons(pageable, keyword, occupation);
    return ResponseEntity.ok(
        ApiResponseDTO.<Page<PersonResponse>>builder()
            .code(HttpStatus.OK.value())
            .message("Lấy danh sách thành công")
            .result(response)
            .build());
  }
}
