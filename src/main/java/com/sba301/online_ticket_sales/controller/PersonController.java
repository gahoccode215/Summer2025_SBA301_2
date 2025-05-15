package com.sba301.online_ticket_sales.controller;

import com.sba301.online_ticket_sales.dto.common.ApiResponse;
import com.sba301.online_ticket_sales.dto.person.request.PersonCreationRequest;
import com.sba301.online_ticket_sales.service.PersonService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Person Controller")
@RequestMapping("/api/v1/persons")
public class PersonController {
    PersonService personService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createPerson(@RequestBody PersonCreationRequest request) {
        personService.createPerson(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .message("Tạo mới thành công")
                .build());
    }
}
