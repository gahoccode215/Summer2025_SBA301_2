package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.model.RedisSecretKey;
import com.sba301.online_ticket_sales.repository.RedisOtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisSecretService {

  private final RedisOtpRepository redisOtpRepository;

  public void saveSecretKey(String key, String secretKey) {
    long createdAt = System.currentTimeMillis();
    long expiresAt = createdAt + 5 * 60 * 1000; // 5 phút

    RedisSecretKey redisOtp =
        RedisSecretKey.builder()
            .id(key)
            .secretKey(secretKey)
            .createdAt(createdAt)
            .expiresAt(expiresAt)
            .build();

    redisOtpRepository.save(redisOtp);
  }

  public String getSecretKey(String key) {
    RedisSecretKey redisOtp =
        redisOtpRepository
            .findById(key)
            .orElseThrow(() -> new AppException(ErrorCode.SECRET_KEY_EXPIRED));

    if (redisOtp.isExpired()) {
      redisOtpRepository.deleteById(key);
      throw new AppException(ErrorCode.SECRET_KEY_EXPIRED);
    }

    return redisOtp.getSecretKey();
  }

  public void removeSecretKey(String key) {
    if (!isOtpExists(key)) {
      throw new AppException(ErrorCode.INVALID_TOKEN);
    }
    redisOtpRepository.deleteById(key);
  }

  public boolean isOtpExists(String key) {
    return redisOtpRepository.existsById(key);
  }

  public boolean isValidSecretKey(String key, String secretKey) {
    String result = getSecretKey(key);
    return result.equals(secretKey);
  }
}
