package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.user.request.CreateUserAccountRequest;
import com.sba301.online_ticket_sales.dto.user.request.UserProfileUpdateRequest;
import com.sba301.online_ticket_sales.dto.user.response.UserProfileResponse;
import com.sba301.online_ticket_sales.dto.user.response.UserResponse;
import com.sba301.online_ticket_sales.entity.User;
import java.util.List;

public interface UserService {

  User getByEmail(String email);

  List<String> getAllRolesByUserId(long userId);

  UserProfileResponse getProfile();

  UserProfileResponse updateProfile(UserProfileUpdateRequest request);

  UserResponse createManagerAccount(CreateUserAccountRequest request);

  UserResponse createStaffAccount(CreateUserAccountRequest request);

  UserResponse createStaffByManager(CreateUserAccountRequest request);
}
