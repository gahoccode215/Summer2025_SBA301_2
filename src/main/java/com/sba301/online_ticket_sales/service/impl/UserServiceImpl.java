package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.constant.PredefinedRole;
import com.sba301.online_ticket_sales.dto.user.request.CreateUserAccountRequest;
import com.sba301.online_ticket_sales.dto.user.request.UserListFilterRequest;
import com.sba301.online_ticket_sales.dto.user.request.UserProfileUpdateRequest;
import com.sba301.online_ticket_sales.dto.user.request.UserUpdateRequest;
import com.sba301.online_ticket_sales.dto.user.response.UserListResponse;
import com.sba301.online_ticket_sales.dto.user.response.UserProfileResponse;
import com.sba301.online_ticket_sales.dto.user.response.UserResponse;
import com.sba301.online_ticket_sales.entity.Cinema;
import com.sba301.online_ticket_sales.entity.Role;
import com.sba301.online_ticket_sales.entity.User;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.enums.UserStatus;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.mapper.UserMapper;
import com.sba301.online_ticket_sales.repository.CinemaRepository;
import com.sba301.online_ticket_sales.repository.RoleRepository;
import com.sba301.online_ticket_sales.repository.UserRepository;
import com.sba301.online_ticket_sales.service.UserService;
import com.sba301.online_ticket_sales.specification.UserSpecification;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final CinemaRepository cinemaRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public User getByEmail(String email) {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new AppException(ErrorCode.EMAIL_OR_PASSWORD_NOT_CORRECT));
  }

  @Override
  public List<String> getAllRolesByUserId(long userId) {
    return userRepository.findAllRolesByUserId(userId);
  }

  @Override
  public UserProfileResponse getProfile() {
    User user = getUserAuthenticated();
    return userMapper.toUserProfileResponse(user);
  }

  @Override
  public UserProfileResponse updateProfile(UserProfileUpdateRequest request) {
    User user = getUserAuthenticated();
    userMapper.updateUserFromProfileRequest(request, user);
    User updatedUser = userRepository.save(user);
    return userMapper.toUserProfileResponse(updatedUser);
  }

  @Override
  @Transactional
  public UserResponse createManagerAccount(CreateUserAccountRequest request) {

    User currentUser = getUserAuthenticated();
    if (!currentUser.isAdmin()) {
      throw new AppException(ErrorCode.ACCESS_DENIED);
    }

    User createdUser = createUserAccount(request, PredefinedRole.MANAGER_ROLE);
    return userMapper.toUserResponse(createdUser);
  }

  @Override
  @Transactional
  public UserResponse createStaffAccount(CreateUserAccountRequest request) {

    User currentUser = getUserAuthenticated();
    if (!currentUser.isAdmin()) {
      throw new AppException(ErrorCode.ACCESS_DENIED);
    }

    User createdUser = createUserAccount(request, PredefinedRole.STAFF_ROLE);
    return userMapper.toUserResponse(createdUser);
  }

  @Override
  @Transactional
  public UserResponse createStaffByManager(CreateUserAccountRequest request) {

    User currentManager = getUserAuthenticated();
    if (!currentManager.isManager()) {
      throw new AppException(ErrorCode.ACCESS_DENIED);
    }

    // Lấy danh sách rạp mà MANAGER đang quản lý
    List<Long> managerCinemaIds =
        currentManager.getManagedCinemas().stream().map(Cinema::getId).collect(Collectors.toList());

    // Kiểm tra rạp được gán cho STAFF_ROLE có nằm trong danh sách rạp của MANAGER không
    if (request.getAssignedCinemaIds() != null && !request.getAssignedCinemaIds().isEmpty()) {
      for (Long cinemaId : request.getAssignedCinemaIds()) {
        if (!managerCinemaIds.contains(cinemaId)) {
          throw new AppException(ErrorCode.MANAGER_CANNOT_ASSIGN_TO_OTHER_CINEMA);
        }
      }
    } else {
      // Nếu không chỉ định rạp, tự động gán tất cả rạp mà MANAGER quản lý
      request.setAssignedCinemaIds(managerCinemaIds);
    }

    User createdUser = createUserAccount(request, PredefinedRole.STAFF_ROLE);
    return userMapper.toUserResponse(createdUser);
  }

  @Override
  public Page<UserListResponse> getAllUsers(UserListFilterRequest filter, Pageable pageable) {

    User currentUser = getUserAuthenticated();

    // Build base specification
    Specification<User> spec = Specification.where(null);

    // Add common filters
    spec =
        spec.and(UserSpecification.hasKeyword(filter.getKeyword()))
            .and(UserSpecification.hasStatus(filter.getStatus()))
            .and(UserSpecification.hasRole(filter.getRoleName()));

    // Add date range filter
    LocalDateTime createdFrom =
        filter.getCreatedFrom() != null ? filter.getCreatedFrom().atStartOfDay() : null;
    LocalDateTime createdTo =
        filter.getCreatedTo() != null ? filter.getCreatedTo().atTime(23, 59, 59) : null;
    spec = spec.and(UserSpecification.createdBetween(createdFrom, createdTo));

    // Apply role-based filtering
    if (currentUser.isAdmin()) {
      // ADMIN: Có thể filter theo cinema và province
      spec =
          spec.and(UserSpecification.belongsToCinema(filter.getCinemaId()))
              .and(UserSpecification.inProvince(filter.getProvince()));
    } else if (currentUser.isManager()) {
      // MANAGER: Chỉ xem users trong rạp mình quản lý
      List<Long> managerCinemaIds =
          currentUser.getManagedCinemas().stream().map(Cinema::getId).collect(Collectors.toList());

      if (managerCinemaIds.isEmpty()) {
        return Page.empty(pageable);
      }

      spec =
          spec.and(UserSpecification.belongsToCinemas(managerCinemaIds))
              .and(
                  UserSpecification.hasRole(PredefinedRole.STAFF_ROLE)
                      .or(UserSpecification.hasRole(PredefinedRole.CUSTOMER_ROLE)));
    } else {
      throw new AppException(ErrorCode.ACCESS_DENIED);
    }

    // Execute query
    Page<User> users = userRepository.findAll(spec, pageable);
    return users.map(userMapper::mapToUserListResponse);
  }

  @Override
  public UserResponse getUserAccountDetail(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    return userMapper.toUserResponse(user);
  }

  @Override
  @Transactional
  public UserResponse updateUser(Long userId, UserUpdateRequest request) {

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

    validateUserUpdate(user, request);

    userMapper.updateUserFromRequest(request, user);

    User updatedUser = userRepository.save(user);

    return userMapper.toUserResponse(updatedUser);
  }

  private void validateUserUpdate(User existingUser, UserUpdateRequest request) {
    User currentUser = getUserAuthenticated();

    // Kiểm tra quyền update
    if (!canUpdateUser(currentUser, existingUser)) {
      throw new AppException(ErrorCode.ACCESS_DENIED);
    }

    // Validate phone uniqueness
    //    if (request.getPhone() != null && !request.getPhone().equals(existingUser.getPhone())) {
    //      if (userRepository.existsByPhoneAndIdNot(request.getPhone(), existingUser.getId())) {
    //        throw new AppException(ErrorCode.PHONE_ALREADY_EXISTS);
    //      }
    //    }

    // Validate status change
    if (request.getStatus() != null) {
      validateStatusChange(existingUser, request.getStatus(), currentUser);
    }

    // Validate role changes
    if (request.getRoleIds() != null && !currentUser.isAdmin()) {
      throw new AppException(ErrorCode.INSUFFICIENT_PERMISSION);
    }
  }

  // THIẾU: Helper methods
  private boolean canUpdateUser(User currentUser, User targetUser) {
    if (currentUser.isAdmin()) {
      return true;
    }

    if (currentUser.isManager() && targetUser.isStaff()) {
      return isUserInManagerCinemas(targetUser, currentUser);
    }

    return currentUser.getId().equals(targetUser.getId());
  }

  private void validateStatusChange(User existingUser, UserStatus newStatus, User currentUser) {
    // Không thể tự disable chính mình
    if (existingUser.getId().equals(currentUser.getId()) && newStatus == UserStatus.INACTIVE) {
      throw new AppException(ErrorCode.CANNOT_DISABLE_SELF);
    }

    // Chỉ ADMIN mới có thể disable/active ADMIN khác
    if (existingUser.isAdmin() && !currentUser.isAdmin()) {
      throw new AppException(ErrorCode.CANNOT_MODIFY_ADMIN);
    }
  }

  private boolean isUserInManagerCinemas(User user, User manager) {
    List<Long> managerCinemaIds =
        manager.getManagedCinemas().stream().map(Cinema::getId).collect(Collectors.toList());

    return user.getManagedCinemas().stream()
        .anyMatch(cinema -> managerCinemaIds.contains(cinema.getId()));
  }

  private User getUserAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication.getPrincipal() instanceof String) {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }
    return (User) authentication.getPrincipal();
  }

  @Transactional
  protected User createUserAccount(CreateUserAccountRequest request, String roleName) {
    // 1. Kiểm tra username đã tồn tại
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
    }

    // 2. Lấy danh sách rạp được gán
    List<Cinema> assignedCinemas = new ArrayList<>();
    if (request.getAssignedCinemaIds() != null && !request.getAssignedCinemaIds().isEmpty()) {
      assignedCinemas = cinemaRepository.findAllById(request.getAssignedCinemaIds());
      if (assignedCinemas.size() != request.getAssignedCinemaIds().size()) {
        throw new AppException(ErrorCode.SOME_CINEMAS_NOT_FOUND);
      }
    }

    // 3. Lấy role theo roleName
    List<Role> roles = roleRepository.findByNameIn(List.of(roleName));
    if (roles.size() != 1) {
      throw new AppException(ErrorCode.INVALID_ROLES);
    }

    User user =
        User.builder()
            .fullName(request.getFullName())
            .username(request.getUsername())
            .phone(request.getPhone())
            .gender(request.getGender())
            .birthDate(request.getBirthDate())
            .address(request.getAddress())
            .password(passwordEncoder.encode(request.getPassword()))
            .status(UserStatus.ACTIVE)
            .roles(roles)
            .managedCinemas(assignedCinemas)
            .build();

    // 6. Lưu user
    return userRepository.save(user);
  }
}
