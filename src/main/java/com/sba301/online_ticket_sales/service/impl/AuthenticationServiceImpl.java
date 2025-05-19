package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.auth.request.LoginRequest;
import com.sba301.online_ticket_sales.dto.auth.request.RegisterRequest;
import com.sba301.online_ticket_sales.dto.auth.response.TokenResponse;
import com.sba301.online_ticket_sales.entity.User;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.enums.TokenType;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.mapper.AuthenticationMapper;
import com.sba301.online_ticket_sales.model.RedisToken;
import com.sba301.online_ticket_sales.repository.UserRepository;
import com.sba301.online_ticket_sales.service.AuthenticationService;
import com.sba301.online_ticket_sales.service.JwtService;
import com.sba301.online_ticket_sales.service.RedisTokenService;
import com.sba301.online_ticket_sales.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sba301.online_ticket_sales.enums.TokenType.ACCESS_TOKEN;
import static com.sba301.online_ticket_sales.enums.TokenType.REFRESH_TOKEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.REFERER;

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
    RedisTokenService redisTokenService;

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
        log.info("REDIS TOKEN {}", RedisToken.builder().id(user.getUsername()).accessToken(accessToken).refreshToken(refreshToken).build());
        redisTokenService.save(RedisToken.builder().id(user.getUsername()).accessToken(accessToken).refreshToken(refreshToken).build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    @Override
    public void logout(HttpServletRequest request) {
        log.info("VAO HAM LOGOUT - {}", request.getHeader(AUTHORIZATION));
        log.info("TOKEN -- {}", request.getHeader(AUTHORIZATION));
        final String authHeader = request.getHeader(AUTHORIZATION);
        if (StringUtils.isBlank(authHeader) || !authHeader.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        final String token = authHeader.substring(7);
        final String email = jwtService.extractEmail(token, ACCESS_TOKEN);
        log.info("EMAIL {}", email);
        redisTokenService.remove(email);
    }

    @Override
    /**
     * Refresh token
     *
     * @param request
     * @return
     */
    public TokenResponse refreshToken(HttpServletRequest request) {
        log.info("---------- refreshToken ----------");

        final String refreshToken = request.getHeader("X-Refresh-Token");
        if (StringUtils.isBlank(refreshToken)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        log.info("Refresh token received: {}", refreshToken);

        final String userName = jwtService.extractEmail(refreshToken, TokenType.REFRESH_TOKEN);
        if (StringUtils.isBlank(userName)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        var user = userService.getByEmail(userName);
        if (!jwtService.isValid(refreshToken, TokenType.REFRESH_TOKEN, user)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        // Xóa access token cũ trong Redis
        redisTokenService.remove(user.getUsername());

        // Tạo access token mới
        String accessToken = jwtService.generateToken(user);

        // Lưu token mới vào Redis
        redisTokenService.save(RedisToken.builder()
                .id(user.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    @Override
    public String forgotPassword(String email) {
        log.info("---------- forgotPassword ----------");

        // check email exists or not
        User user = userService.getByEmail(email);

        // generate reset token
        String resetToken = jwtService.generateResetToken(user);

        // save to db
        // tokenService.save(Token.builder().username(user.getUsername()).resetToken(resetToken).build());
        redisTokenService.save(RedisToken.builder().id(user.getUsername()).resetToken(resetToken).build());

        // TODO send email to user
        String confirmLink = String.format("curl --location 'http://localhost:80/auth/reset-password' \\\n" +
                "--header 'accept: */*' \\\n" +
                "--header 'Content-Type: application/json' \\\n" +
                "--data '%s'", resetToken);
        log.info("--> confirmLink: {}", confirmLink);

        return resetToken;
    }
}
