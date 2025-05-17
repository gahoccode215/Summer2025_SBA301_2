package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.model.RedisToken;

public interface RedisTokenService {
    void save(RedisToken token);
    void remove(String id);
    boolean isExists(String id);
}
