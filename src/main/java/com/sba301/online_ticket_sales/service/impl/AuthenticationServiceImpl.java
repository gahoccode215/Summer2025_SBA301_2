package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.identity.Credential;
import com.sba301.online_ticket_sales.dto.identity.TokenExchangeParam;
import com.sba301.online_ticket_sales.dto.identity.UserCreationParam;
import com.sba301.online_ticket_sales.dto.request.RegisterRequest;
import com.sba301.online_ticket_sales.entity.User;
//import com.sba301.online_ticket_sales.mapper.AuthenticationMapper;
import com.sba301.online_ticket_sales.mapper.AuthenticationMapper;
import com.sba301.online_ticket_sales.repository.IdentityClient;
import com.sba301.online_ticket_sales.repository.UserRepository;
import com.sba301.online_ticket_sales.service.AuthenticationService;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    IdentityClient identityClient;
    AuthenticationMapper authenticationMapper;
    UserRepository userRepository;

    @Value("${idp.client-id}")
    @NonFinal
    String clientId;

    @Value("${idp.client-secret}")
    @NonFinal
    String clientSecret;


    @Override
    public void register(RegisterRequest request) {
        try {
            log.info("CLIENT ID: {}", clientId);
            // Create account in KeyCloak
            // Exchange client Token
            var token = identityClient.exchangeToken(TokenExchangeParam.builder()
                    .grant_type("client_credentials")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .scope("openid")
                    .build());
            log.info("TokenInfo {}", token);
            // Create user with client Token and given info
            // Get userId of keyCloak account
            var creationResponse = identityClient.createUser(
                    "Bearer " + token.getAccessToken(),
                    UserCreationParam.builder()
                            .username(request.getEmail())
                            .firstName(request.getFullName())
                            .lastName(request.getFullName())
                            .email(request.getEmail())
                            .enabled(true)
                            .emailVerified(false)
                            .credentials(List.of(Credential.builder()
                                    .type("password")
                                    .temporary(false)
                                    .value(request.getPassword())
                                    .build()))
                            .build());

            // Assign userId of keycloak to User
            String userId = extractUserId(creationResponse);
            log.info("UserId {}", userId);

            User user = authenticationMapper.toUser(request);
            user.setUserId(userId);
            userRepository.save(user);
        } catch (FeignException ex) {
            log.error(ex.getMessage());
        }
    }

    private String extractUserId(ResponseEntity<?> response) {
        String location = response.getHeaders().get("Location").getFirst();
        String[] splitedStr = location.split("/");
        return splitedStr[splitedStr.length - 1];
    }
}
