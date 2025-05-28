package com.sba301.online_ticket_sales.controller;

import com.sba301.online_ticket_sales.dto.common.ApiResponse;
import com.sba301.online_ticket_sales.dto.person.request.PersonCreationRequest;
import com.sba301.online_ticket_sales.dto.person.request.PersonUpdateRequest;
import com.sba301.online_ticket_sales.dto.person.response.PersonResponse;
import com.sba301.online_ticket_sales.enums.Occupation;
import com.sba301.online_ticket_sales.service.PersonService;
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
@Tag(name = "Person Controller")
@RequestMapping("/api/v1/persons")
public class PersonController {
    PersonService personService;

    @PostMapping
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<PersonResponse>> createPerson(@Valid @RequestBody PersonCreationRequest request) {
        PersonResponse response = personService.createPerson(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<PersonResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Tạo mới thành công")
                        .result(response)
                        .build());
    }
    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<PersonResponse>> updatePerson(
            @PathVariable Integer id,
            @Valid @RequestBody PersonUpdateRequest request) {
        PersonResponse response = personService.updatePerson(id, request);
        return ResponseEntity.ok(ApiResponse.<PersonResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Cập nhật thành công")
                .result(response)
                .build());
    }
    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deletePerson(@PathVariable Integer id) {
        personService.deletePerson(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Xóa thành công")
                .build());
    }
    @GetMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<PersonResponse>> getPersonDetail(@PathVariable Integer id) {
        PersonResponse response = personService.getPersonDetail(id);
        return ResponseEntity.ok(ApiResponse.<PersonResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy chi tiết thành công")
                .result(response)
                .build());
    }
    @GetMapping
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<Page<PersonResponse>>> getAllPersons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Occupation occupation,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(
                Sort.Direction.fromString(sortDir), sortBy));
        Page<PersonResponse> response = personService.getAllPersons(pageable, keyword, occupation);
        return ResponseEntity.ok(ApiResponse.<Page<PersonResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy danh sách thành công")
                .result(response)
                .build());
    }
}
