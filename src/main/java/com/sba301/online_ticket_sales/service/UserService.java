package com.sba301.online_ticket_sales.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

public interface UserService {
    UserDetailsService userDetailsService();
}
