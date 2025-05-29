package com.sba301.online_ticket_sales.model;

import java.io.Serializable;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("RedisToken")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RedisToken implements Serializable {
  private String id;
  private String accessToken;
  private String refreshToken;
  private String resetToken;
}
