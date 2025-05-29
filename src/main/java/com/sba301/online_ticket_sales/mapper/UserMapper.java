package com.sba301.online_ticket_sales.mapper;

import com.sba301.online_ticket_sales.dto.user.request.UserProfileUpdateRequest;
import com.sba301.online_ticket_sales.dto.user.response.UserProfileResponse;
import com.sba301.online_ticket_sales.entity.User;

public interface UserMapper {
    UserProfileResponse toUserProfileResponse(User user);
    void updateUserFromProfileRequest(UserProfileUpdateRequest updateRequest, User user);
}
