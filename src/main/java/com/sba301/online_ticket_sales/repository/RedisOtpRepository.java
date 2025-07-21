package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.model.RedisSecretKey;
import org.springframework.data.repository.CrudRepository;

public interface RedisOtpRepository extends CrudRepository<RedisSecretKey, String> {

  void save(String key, String otp);

  boolean existsById(String key);
}
