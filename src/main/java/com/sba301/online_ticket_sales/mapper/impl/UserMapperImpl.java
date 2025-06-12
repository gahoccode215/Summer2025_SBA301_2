package com.sba301.online_ticket_sales.mapper.impl;

import com.sba301.online_ticket_sales.dto.cinema.response.CinemaResponse;
import com.sba301.online_ticket_sales.dto.user.request.UserProfileUpdateRequest;
import com.sba301.online_ticket_sales.dto.user.response.UserProfileResponse;
import com.sba301.online_ticket_sales.dto.user.response.UserResponse;
import com.sba301.online_ticket_sales.entity.Cinema;
import com.sba301.online_ticket_sales.entity.Role;
import com.sba301.online_ticket_sales.entity.User;
import com.sba301.online_ticket_sales.mapper.UserMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapperImpl implements UserMapper {

  private final PasswordEncoder passwordEncoder;

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
