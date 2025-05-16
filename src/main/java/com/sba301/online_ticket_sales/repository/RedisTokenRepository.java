package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.model.RedisToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RedisTokenRepository extends JpaRepository<RedisToken, String> {
}
