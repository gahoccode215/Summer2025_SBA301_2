package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    UserDetailsService userDetailsService();
    User getByEmail(String email);
    List<String> getAllRolesByUserId(long userId);
}
