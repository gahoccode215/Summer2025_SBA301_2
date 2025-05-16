package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.auth.request.RegisterRequest;
import com.sba301.online_ticket_sales.entity.User;
import com.sba301.online_ticket_sales.mapper.AuthenticationMapper;
import com.sba301.online_ticket_sales.repository.UserRepository;
import com.sba301.online_ticket_sales.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
        import lombok.extern.slf4j.Slf4j;
        import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {

        UserRepository userRepository;
        AuthenticationMapper authenticationMapper;

        @Override
        public void register(RegisterRequest request) {
                userRepository.save(authenticationMapper.toUser(request));
        }
}
