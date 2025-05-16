package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.service.UserService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public UserDetailsService userDetailsService() {
        return null;
    }
}
