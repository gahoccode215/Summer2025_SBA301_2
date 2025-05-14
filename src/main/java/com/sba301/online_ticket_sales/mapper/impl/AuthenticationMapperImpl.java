package com.sba301.online_ticket_sales.mapper.impl;

import com.sba301.online_ticket_sales.dto.request.RegisterRequest;
import com.sba301.online_ticket_sales.entity.User;
import com.sba301.online_ticket_sales.mapper.AuthenticationMapper;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationMapperImpl implements AuthenticationMapper {
    @Override
    public User toUser(RegisterRequest request) {
        return User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .password(request.getPassword())
                .build();
    }
}
