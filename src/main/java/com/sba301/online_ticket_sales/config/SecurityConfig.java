package com.sba301.online_ticket_sales.config;

import com.sba301.online_ticket_sales.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  private final UserRepository userRepository;
  private final PreFilter preFilter;

  private final String[] PUBLIC_ENDPOINTS = {
    "/api/v1/auth/**",
    "/**",
    "/api/v1/movies/**",
    "/api/v1/schedules/**",
    "/api/v1/cinemas/**",
    "/api/v1/rooms/**",
    "/api/v1/payments/**",
    "/api/v1/persons/**",
    "/api/v1/genres/**",
    "/api/v1/countries/**"
  };

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            request ->
                request.requestMatchers(PUBLIC_ENDPOINTS).permitAll().anyRequest().authenticated())
        .authenticationProvider(provider())
        .addFilterBefore(preFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(
            exceptions ->
                exceptions
                    .authenticationEntryPoint(
                        (request, response, authException) -> {
                          // Xử lý 401 - Không có token hoặc token không hợp lệ
                          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                          response.setContentType("application/json");
                          response.setCharacterEncoding("UTF-8");
                          response
                              .getWriter()
                              .write(
                                  "{\"code\": 401, \"message\": \"Vui lòng đăng nhập để sử dụng chức năng này\"}");
                        })
                    .accessDeniedHandler(
                        (request, response, accessDeniedException) -> {
                          // Xử lý 403 - Có token nhưng không đủ quyền
                          response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                          response.setContentType("application/json");
                          response.setCharacterEncoding("UTF-8");
                          response
                              .getWriter()
                              .write(
                                  "{\"code\": 403, \"message\": \"Bạn không có quyền thực hiện chức năng này\"}");
                        }));
    return httpSecurity.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedOrigins(List.of("http://localhost:5173"));
    corsConfiguration.setAllowedMethods(
        List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    corsConfiguration.setAllowedHeaders(List.of("*"));
    corsConfiguration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfiguration);
    return source;
  }

  @Bean
  public WebSecurityCustomizer ignoreResources() {
    return webSecurity ->
        webSecurity
            .ignoring()
            .requestMatchers(
                "/actuator/**",
                "/v3/**",
                "/webjars/**",
                "/swagger-ui*/*swagger-initializer.js",
                "/swagger-ui*/**",
                "/favicon.ico");
  }

  @Bean
  public PasswordEncoder getPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public AuthenticationProvider provider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService());
    provider.setPasswordEncoder(getPasswordEncoder());

    return provider;
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return identifier -> {

      // Tìm user theo username hoặc email
      return userRepository
          .findByUsernameOrEmail(identifier)
          .orElseThrow(
              () -> {
                return new UsernameNotFoundException("User not found: " + identifier);
              });
    };
  }
}
