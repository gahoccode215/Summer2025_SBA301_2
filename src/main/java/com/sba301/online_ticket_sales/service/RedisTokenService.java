package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.model.RedisToken;

public interface RedisTokenService {
    void save(RedisToken token);
    void remove(String id);
    boolean isExists(String id);
    RedisToken getById(String id); // Thêm phương thức lấy RedisToken theo id
    boolean isValidToken(String id, String accessToken); // Thêm phương thức kiểm tra accessToken
}
