package com.sba301.online_ticket_sales.controller;

import com.sba301.online_ticket_sales.dto.common.ApiResponseDTO;
import com.sba301.online_ticket_sales.dto.user.request.CreateUserAccountRequest;
import com.sba301.online_ticket_sales.dto.user.request.UserListFilterRequest;
import com.sba301.online_ticket_sales.dto.user.request.UserProfileUpdateRequest;
import com.sba301.online_ticket_sales.dto.user.request.UserUpdateRequest;
import com.sba301.online_ticket_sales.dto.user.response.UserListResponse;
import com.sba301.online_ticket_sales.dto.user.response.UserProfileResponse;
import com.sba301.online_ticket_sales.dto.user.response.UserResponse;
import com.sba301.online_ticket_sales.enums.UserStatus;
import com.sba301.online_ticket_sales.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
            description =
                    "Lấy thông tin hồ sơ của người dùng hiện tại dựa trên JWT token. Yêu cầu người dùng đã đăng nhập.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lấy hồ sơ thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập hoặc token không hợp lệ",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Người dùng không tồn tại", content = @Content)
    })
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CUSTOMER', 'STAFF')")
    public ResponseEntity<ApiResponseDTO<UserProfileResponse>> getProfile() {
        UserProfileResponse response = userService.getProfile();
        return ResponseEntity.ok(
                ApiResponseDTO.<UserProfileResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy hồ sơ thành công")
                        .result(response)
                        .build());
    }

    @Operation(
            summary = "Cập nhật thông tin hồ sơ người dùng",
            description =
                    "Cập nhật thông tin hồ sơ (tên, ngày sinh, giới tính) của người dùng hiện tại dựa trên JWT token. Yêu cầu người dùng đã đăng nhập.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cập nhật hồ sơ thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dữ liệu đầu vào không hợp lệ",
                    content = @Content),
            @ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập hoặc token không hợp lệ",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Người dùng không tồn tại", content = @Content)
    })
    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CUSTOMER', 'STAFF')")
    public ResponseEntity<ApiResponseDTO<UserProfileResponse>> updateProfile(
            @Valid @RequestBody UserProfileUpdateRequest request) {
        UserProfileResponse response = userService.updateProfile(request);
        return ResponseEntity.ok(
                ApiResponseDTO.<UserProfileResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Cập nhật hồ sơ thành công")
                        .result(response)
                        .build());
    }

    @PostMapping("/managers")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "ADMIN tạo tài khoản MANAGER",
            description = "ADMIN tạo tài khoản MANAGER và gán rạp quản lý")
    public ResponseEntity<UserResponse> createManager(
            @Valid @RequestBody CreateUserAccountRequest request) {
        UserResponse response = userService.createManagerAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/staffs")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "ADMIN tạo tài khoản STAFF_ROLE",
            description = "ADMIN tạo tài khoản STAFF_ROLE và gán rạp làm việc")
    public ResponseEntity<UserResponse> createStaff(
            @Valid @RequestBody CreateUserAccountRequest request) {
        UserResponse response = userService.createStaffAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/staff/by-manager")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
            summary = "MANAGER tạo tài khoản STAFF_ROLE",
            description = "MANAGER tạo tài khoản STAFF_ROLE trong rạp mình quản lý")
    public ResponseEntity<UserResponse> createStaffByManager(
            @Valid @RequestBody CreateUserAccountRequest request) {
        UserResponse response = userService.createStaffByManager(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Lấy danh sách tài khoản")
    public ResponseEntity<ApiResponseDTO<Page<UserListResponse>>> getAllUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Long cinemaId,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate createdTo,

            // Pagination parameters
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        // Tạo Pageable từ parameters
        Sort sort =
                Sort.by(
                        sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // Tạo filter object
        UserListFilterRequest filter =
                UserListFilterRequest.builder()
                        .keyword(keyword)
                        .status(status)
                        .roleName(roleName)
                        .cinemaId(cinemaId)
                        .province(province)
                        .createdFrom(createdFrom)
                        .createdTo(createdTo)
                        .build();

        Page<UserListResponse> users = userService.getAllUsers(filter, pageable);

        return ResponseEntity.ok(
                ApiResponseDTO.<Page<UserListResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy danh sách tài khoản thành công")
                        .result(users)
                        .build());
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Lấy chi tiết tài khoản")
    public ResponseEntity<ApiResponseDTO<UserResponse>> getUserDetail(@PathVariable Long userId) {
        var result = userService.getUserAccountDetail(userId);
        return ResponseEntity.ok(ApiResponseDTO.<UserResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy chi tiết tài khoản thành công")
                .result(result)
                .build());
    }
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or @userService.canUpdateUser(#userId)")
    @Operation(summary = "Cập nhật thông tin tài khoản",
            description = "Update user profile, status (disable/active), roles, cinema assignments")
    public ResponseEntity<ApiResponseDTO<UserResponse>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequest request) {

        UserResponse response = userService.updateUser(userId, request);

        return ResponseEntity.ok(
                ApiResponseDTO.<UserResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Cập nhật tài khoản thành công")
                        .result(response)
                        .build());
    }
    /**
     * Vô hiệu hóa tài khoản (convenience endpoint)
     */
    @PatchMapping("/{userId}/disable")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Vô hiệu hóa tài khoản")
    public ResponseEntity<ApiResponseDTO<UserResponse>> disableUser(@PathVariable Long userId) {
        UserUpdateRequest request = UserUpdateRequest.builder()
                .status(UserStatus.INACTIVE)
                .build();

        UserResponse response = userService.updateUser(userId, request);

        return ResponseEntity.ok(
                ApiResponseDTO.<UserResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Vô hiệu hóa tài khoản thành công")
                        .result(response)
                        .build());
    }

    /**
     * Kích hoạt tài khoản (convenience endpoint)
     */
    @PatchMapping("/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Kích hoạt tài khoản")
    public ResponseEntity<ApiResponseDTO<UserResponse>> activateUser(@PathVariable Long userId) {
        UserUpdateRequest request = UserUpdateRequest.builder()
                .status(UserStatus.ACTIVE)
                .build();

        UserResponse response = userService.updateUser(userId, request);

        return ResponseEntity.ok(
                ApiResponseDTO.<UserResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Kích hoạt tài khoản thành công")
                        .result(response)
                        .build());
    }
}
