package com.sba301.online_ticket_sales.mapper.impl;

import com.sba301.online_ticket_sales.constant.PredefinedRole;
import com.sba301.online_ticket_sales.dto.auth.request.RegisterRequest;
import com.sba301.online_ticket_sales.entity.Role;
import com.sba301.online_ticket_sales.entity.User;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.mapper.AuthenticationMapper;
import com.sba301.online_ticket_sales.repository.RoleRepository;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationMapperImpl implements AuthenticationMapper {

  private final PasswordEncoder passwordEncoder;
  private final RoleRepository roleRepository;

  @Override
  public User toUser(RegisterRequest request) {
    Role customerRole =
        roleRepository
            .findByName(PredefinedRole.CUSTOMER_ROLE)
            .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
    return User.builder()
        .fullName(request.getFullName())
        .email(request.getEmail())
        .birthDate(request.getBirthDate())
        .gender(request.getGender())
        .roles(Collections.singletonList(customerRole))
        .password(passwordEncoder.encode(request.getPassword()))
        .build();
  }
}
