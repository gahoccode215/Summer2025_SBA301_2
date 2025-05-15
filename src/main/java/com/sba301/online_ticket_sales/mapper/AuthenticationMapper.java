package com.sba301.online_ticket_sales.mapper;

import com.sba301.online_ticket_sales.dto.auth.request.RegisterRequest;
import com.sba301.online_ticket_sales.entity.User;

public interface AuthenticationMapper {
    User toUser(RegisterRequest request);
}
