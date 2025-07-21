package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.model.RedisToken;
import com.sba301.online_ticket_sales.repository.RedisTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisTokenService {
  private final RedisTokenRepository redisTokenRepository;

  public void save(RedisToken redisToken) {
    redisTokenRepository.save(redisToken);
  }

  public void remove(String id) {
    isExists(id);
    redisTokenRepository.deleteById(id);
  }

  public boolean isExists(String id) {
    if (!redisTokenRepository.existsById(id)) {
      throw new AppException(ErrorCode.INVALID_TOKEN);
    }
    return true;
  }

  public RedisToken getById(String id) {
    return redisTokenRepository
        .findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));
  }

  public boolean isValidToken(String id, String accessToken) {
    RedisToken redisToken = getById(id);
    return redisToken.getAccessToken().equals(accessToken);
  }
}
