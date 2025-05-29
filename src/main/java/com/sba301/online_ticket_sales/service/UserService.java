package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.entity.User;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
  UserDetailsService userDetailsService();

  User getByEmail(String email);

  List<String> getAllRolesByUserId(long userId);
}
