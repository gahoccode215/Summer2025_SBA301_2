package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.user.request.UserProfileUpdateRequest;
import com.sba301.online_ticket_sales.dto.user.response.UserProfileResponse;
import com.sba301.online_ticket_sales.entity.User;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.mapper.UserMapper;
import com.sba301.online_ticket_sales.repository.UserRepository;
import com.sba301.online_ticket_sales.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public UserDetailsService userDetailsService() {
    return email ->
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

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

  private User getUserAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication.getPrincipal() instanceof String) {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }
    return (User) authentication.getPrincipal();
  }
}
