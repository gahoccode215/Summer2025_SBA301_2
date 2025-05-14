package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
