package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.constant.PredefinedRole;
import com.sba301.online_ticket_sales.dto.user.request.CreateUserAccountRequest;
import com.sba301.online_ticket_sales.dto.user.request.UserProfileUpdateRequest;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    User createdUser = createUserAccount(request, PredefinedRole.STAFF);
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

    // Kiểm tra rạp được gán cho STAFF có nằm trong danh sách rạp của MANAGER không
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

    User createdUser = createUserAccount(request, PredefinedRole.STAFF);
    return userMapper.toUserResponse(createdUser);
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
