package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.model.RedisToken;
import com.sba301.online_ticket_sales.repository.RedisTokenRepository;
import com.sba301.online_ticket_sales.service.RedisTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisTokenServiceImpl implements RedisTokenService {
    private final RedisTokenRepository redisTokenRepository;

    @Override
    public void save(RedisToken token) {
        redisTokenRepository.save(token);
    }

    @Override
    public void remove(String id) {
        isExists(id);
        redisTokenRepository.deleteById(id);
    }

    @Override
    public boolean isExists(String id) {
        if (!redisTokenRepository.existsById(id)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        return true;
    }
}
