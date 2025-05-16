package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
