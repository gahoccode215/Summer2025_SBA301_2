package com.sba301.online_ticket_sales.controller;

import com.sba301.online_ticket_sales.dto.common.ApiResponseDTO;
import com.sba301.online_ticket_sales.dto.user.response.UserProfileResponse;
import com.sba301.online_ticket_sales.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "User Controller", description = "APIs quản lý người dùng")
@RequestMapping("/api/v1/users")
public class UserController {

    UserService userService;

    @Operation(
            summary = "Lấy thông tin hồ sơ người dùng",
            description = "Lấy thông tin hồ sơ của người dùng hiện tại dựa trên JWT token. Yêu cầu người dùng đã đăng nhập."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy hồ sơ thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập hoặc token không hợp lệ",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Người dùng không tồn tại",
                    content = @Content)
    })
    @GetMapping("/profile")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<ApiResponseDTO<UserProfileResponse>> getProfile() {
        UserProfileResponse response = userService.getProfile();
        return ResponseEntity.ok(ApiResponseDTO.<UserProfileResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy hồ sơ thành công")
                .result(response)
                .build());
    }
}
