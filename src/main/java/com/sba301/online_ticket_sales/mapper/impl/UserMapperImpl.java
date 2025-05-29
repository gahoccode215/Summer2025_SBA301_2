package com.sba301.online_ticket_sales.mapper.impl;

import com.sba301.online_ticket_sales.dto.user.request.UserProfileUpdateRequest;
import com.sba301.online_ticket_sales.dto.user.response.UserProfileResponse;
import com.sba301.online_ticket_sales.entity.User;
import com.sba301.online_ticket_sales.mapper.UserMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapperImpl implements UserMapper {

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
}
