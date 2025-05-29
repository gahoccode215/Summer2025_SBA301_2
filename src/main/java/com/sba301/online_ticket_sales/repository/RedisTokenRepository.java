package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.model.RedisToken;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface RedisTokenRepository extends CrudRepository<RedisToken, String> {
  Optional<RedisToken> findById(String id);
}
