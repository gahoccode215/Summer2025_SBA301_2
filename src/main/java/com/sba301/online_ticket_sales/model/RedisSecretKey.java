package com.sba301.online_ticket_sales.model;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "RedisSecretKey", timeToLive = 300)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RedisSecretKey {
    @Id
    private String id;
    private String secretKey;
    private String email;
    private long createdAt;
    private long expiresAt;

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
}