package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.user.request.UserProfileUpdateRequest;
import com.sba301.online_ticket_sales.dto.user.response.UserProfileResponse;
import com.sba301.online_ticket_sales.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    UserDetailsService userDetailsService();
    User getByEmail(String email);
    List<String> getAllRolesByUserId(long userId);
    UserProfileResponse getProfile();
    UserProfileResponse updateProfile(UserProfileUpdateRequest request);
}
