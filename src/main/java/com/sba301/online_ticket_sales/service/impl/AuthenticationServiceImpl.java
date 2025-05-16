package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.auth.request.LoginRequest;
import com.sba301.online_ticket_sales.dto.auth.request.RegisterRequest;
import com.sba301.online_ticket_sales.dto.auth.response.TokenResponse;
import com.sba301.online_ticket_sales.entity.User;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.mapper.AuthenticationMapper;
import com.sba301.online_ticket_sales.repository.UserRepository;
import com.sba301.online_ticket_sales.service.AuthenticationService;
import com.sba301.online_ticket_sales.service.JwtService;
import com.sba301.online_ticket_sales.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    UserRepository userRepository;
    AuthenticationMapper authenticationMapper;
    UserService userService;
    AuthenticationManager authenticationManager;
    JwtService jwtService;

    @Override
    public void register(RegisterRequest request) {
        userRepository.save(authenticationMapper.toUser(request));
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        User user = userService.getByEmail(request.getEmail());
        if(!user.isEnabled()){
            throw new AppException(ErrorCode.ACCOUNT_HAS_BEEN_DISABLE);
        }
        List<String> roles = userService.getAllRolesByUserId(user.getId());
        List<SimpleGrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).toList();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword(), authorities));
        }catch (BadCredentialsException e){
            throw new AppException(ErrorCode.EMAIL_OR_PASSWORD_NOT_CORRECT);
        }


        // create new access token
        String accessToken = jwtService.generateToken(user);

        // create new refresh token
        String refreshToken = jwtService.generateRefreshToken(user);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }
}
