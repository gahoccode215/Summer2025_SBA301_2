package com.sba301.online_ticket_sales.repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrderSeatRedisRepository {
  private static final String SEAT_HOLD_KEY_PREFIX = "seatHold_showtimeId_";

  private final StringRedisTemplate redisTemplate;

  public OrderSeatRedisRepository(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void save(String key, String value, Duration ttl) {
    redisTemplate.opsForValue().set(key, value, ttl);
  }

  public void delete(String key) {
    redisTemplate.delete(key);
  }

  public void deleteList(List<String> keys) {
    redisTemplate.delete(keys);
  }

  public Optional<String> get(String key) {
    String value = redisTemplate.opsForValue().get(key);
    return Optional.ofNullable(value);
  }

  public Set<String> getKeysByPattern(String pattern) {
    return redisTemplate.keys(pattern);
  }

  public boolean exists(String key) {
    return redisTemplate.hasKey(key);
  }

  public void releaseSeat(Long showtimeId, List<String> seatCodes) {
    List<String> keys =
        seatCodes.stream().map(seat -> SEAT_HOLD_KEY_PREFIX + showtimeId + ":" + seat).toList();
    redisTemplate.delete(keys);
  }
}
