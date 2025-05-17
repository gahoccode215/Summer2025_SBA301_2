package com.sba301.online_ticket_sales.config;

import static com.sba301.online_ticket_sales.enums.TokenType.ACCESS_TOKEN;

import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.service.JwtService;
import com.sba301.online_ticket_sales.service.RedisTokenService;
import com.sba301.online_ticket_sales.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;



import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@Slf4j
@RequiredArgsConstructor
public class PreFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtService jwtService;
    private final RedisTokenService redisTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.info("---------- doFilterInternal ----------");

        final String authHeader = request.getHeader(AUTHORIZATION);
        log.info("Authorization: {}", authHeader);

        if (StringUtils.isBlank(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        final String token = authHeader.substring(7);
        final String email = jwtService.extractEmail(token, ACCESS_TOKEN);
        if (StringUtils.isBlank(email)) {
            log.warn("Token JWT không hợp lệ");
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Kiểm tra token có tồn tại trong Redis
        if (!redisTokenService.isExists(email)) {
            log.warn("Token cho email {} đã bị vô hiệu hóa", email);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }


        if (StringUtils.isNotEmpty(email) && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.info("DAY NE");
            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(email);
            if (jwtService.isValid(token, ACCESS_TOKEN, userDetails)) {
                log.info("ALO ALO");
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
                log.info("TOI DUOC DAY");
            }
        }

        filterChain.doFilter(request, response);
    }
}
