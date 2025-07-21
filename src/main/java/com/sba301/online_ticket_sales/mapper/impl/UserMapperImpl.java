package com.sba301.online_ticket_sales.mapper.impl;

import com.sba301.online_ticket_sales.dto.cinema.response.CinemaResponse;
import com.sba301.online_ticket_sales.dto.user.request.UserProfileUpdateRequest;
import com.sba301.online_ticket_sales.dto.user.request.UserUpdateRequest;
import com.sba301.online_ticket_sales.dto.user.response.UserListResponse;
import com.sba301.online_ticket_sales.dto.user.response.UserProfileResponse;
import com.sba301.online_ticket_sales.dto.user.response.UserResponse;
import com.sba301.online_ticket_sales.entity.Cinema;
import com.sba301.online_ticket_sales.entity.Role;
import com.sba301.online_ticket_sales.entity.User;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.mapper.UserMapper;
import com.sba301.online_ticket_sales.repository.CinemaRepository;
import com.sba301.online_ticket_sales.repository.RoleRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserMapperImpl implements UserMapper {

  RoleRepository roleRepository;
  CinemaRepository cinemaRepository;

  @Override
  public UserProfileResponse toUserProfileResponse(User user) {
    UserProfileResponse response = new UserProfileResponse();
    response.setId(user.getId());
    response.setEmail(user.getEmail());
    response.setFullName(user.getFullName());
    response.setBirthDate(user.getBirthDate());
    response.setGender(user.getGender());
    return response;
  }

  @Override
  public void updateUserFromProfileRequest(UserProfileUpdateRequest updateRequest, User user) {
    Optional.ofNullable(updateRequest.getFullName())
        .filter(name -> !name.isBlank())
        .ifPresent(user::setFullName);
    Optional.ofNullable(updateRequest.getBirthDate()).ifPresent(user::setBirthDate);
    Optional.ofNullable(updateRequest.getGender()).ifPresent(user::setGender);
    Optional.ofNullable(updateRequest.getPhone()).ifPresent(user::setPhone);
  }

  @Override
  public UserResponse toUserResponse(User user) {
    if (user == null) {
      return null;
    }

    UserResponse response = new UserResponse();

    // Map các trường cơ bản
    response.setId(user.getId());
    response.setFullName(user.getFullName());
    response.setEmail(user.getEmail());
    response.setUsername(user.getUsername());
    response.setPhone(user.getPhone());
    response.setGender(user.getGender());
    response.setBirthDate(user.getBirthDate());
    response.setAddress(user.getAddress());
    response.setStatus(user.getStatus());
    response.setCreatedAt(user.getCreatedAt());
    response.setUpdatedAt(user.getUpdatedAt());
    response.setPassword(user.getPassword());

    // Map roles từ List<Role> sang List<String>
    response.setRoles(mapRolesToStringList(user.getRoles()));

    // Map working cinemas từ managedCinemas
    response.setWorkingCinemas(mapCinemasToResponseList(user.getManagedCinemas()));

    return response;
  }

  @Override
  public UserListResponse mapToUserListResponse(User user) {
    List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());

    List<UserListResponse.CinemaInfo> cinemaInfos =
        user.getManagedCinemas().stream()
            .map(
                cinema ->
                    UserListResponse.CinemaInfo.builder()
                        .id(cinema.getId())
                        .name(cinema.getName())
                        .address(cinema.getAddress())
                        .province(cinema.getProvince())
                        .isActive(cinema.isActive())
                        .build())
            .collect(Collectors.toList());

    return UserListResponse.builder()
        .id(user.getId())
        .fullName(user.getFullName())
        .username(user.getUsername())
        .email(user.getEmail())
        .phone(user.getPhone())
        .gender(user.getGender())
        .status(user.getStatus())
        .birthDate(user.getBirthDate())
        .address(user.getAddress())
        .roles(roles)
        .workingCinemas(cinemaInfos)
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .build();
  }

  @Override
  public void updateUserFromRequest(UserUpdateRequest request, User user) {
    Optional.ofNullable(request.getFullName())
        .filter(name -> !name.isBlank())
        .ifPresent(user::setFullName);
    Optional.ofNullable(request.getPhone()).ifPresent(user::setPhone);
    Optional.ofNullable(request.getGender()).ifPresent(user::setGender);
    Optional.ofNullable(request.getBirthDate()).ifPresent(user::setBirthDate);
    Optional.ofNullable(request.getAddress()).ifPresent(user::setAddress);
    Optional.ofNullable(request.getStatus()).ifPresent(user::setStatus);
    if (request.getRoleIds() != null) {
      updateUserRoles(user, request.getRoleIds());
    }
    if (request.getAssignedCinemaIds() != null) {
      updateManagedCinemas(user, request.getAssignedCinemaIds());
    }
  }

  private void updateUserRoles(User user, List<Integer> roleIds) {
    List<Role> roles = roleRepository.findAllById(roleIds);
    if (roles.size() != roleIds.size()) {
      throw new AppException(ErrorCode.INVALID_ROLES);
    }
    user.setRoles(roles);
  }

  private void updateManagedCinemas(User user, List<Long> cinemaIds) {
    if (cinemaIds.isEmpty()) {
      user.setManagedCinemas(new ArrayList<>());
    } else {
      List<Cinema> cinemas = cinemaRepository.findAllById(cinemaIds);
      if (cinemas.size() != cinemaIds.size()) {
        throw new AppException(ErrorCode.SOME_CINEMAS_NOT_FOUND);
      }
      user.setManagedCinemas(cinemas);
    }
  }

  /** Map roles từ List<Role> sang List<String> */
  private List<String> mapRolesToStringList(List<Role> roles) {
    if (roles == null || roles.isEmpty()) {
      return new ArrayList<>();
    }

    return roles.stream()
        .filter(role -> role != null && role.getName() != null)
        .map(Role::getName)
        .collect(Collectors.toList());
  }

  /** Map cinemas từ List<Cinema> sang List<CinemaResponse> */
  private List<CinemaResponse> mapCinemasToResponseList(List<Cinema> cinemas) {
    if (cinemas == null || cinemas.isEmpty()) {
      return new ArrayList<>();
    }

    return cinemas.stream()
        .filter(cinema -> cinema != null)
        .map(this::mapCinemaToResponse)
        .collect(Collectors.toList());
  }

  /** Map Cinema entity sang CinemaResponse */
  private CinemaResponse mapCinemaToResponse(Cinema cinema) {
    CinemaResponse response = new CinemaResponse();
    response.setId(cinema.getId());
    response.setName(cinema.getName());
    response.setHotline(cinema.getHotline());
    response.setAddress(cinema.getAddress());
    response.setProvince(cinema.getProvince());
    response.setActive(cinema.isActive());
    return response;
  }
}
