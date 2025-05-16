package com.sba301.online_ticket_sales.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.sba301.online_ticket_sales.dto.identity.KeyCloakError;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import org.springframework.stereotype.Component;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ErrorNormalizer {
    private final ObjectMapper objectMapper;
    private final Map<String, ErrorCode> errorCodeMap;

    public ErrorNormalizer() {
        objectMapper = new ObjectMapper();
        errorCodeMap = new HashMap<>();

        errorCodeMap.put("User exists with same email", ErrorCode.EMAIL_EXSITED);
        errorCodeMap.put("invalid_grant", ErrorCode.EMAIL_OR_PASSWORD_NOT_CORRECT);
        errorCodeMap.put("Invalid user credentials", ErrorCode.EMAIL_OR_PASSWORD_NOT_CORRECT);
        errorCodeMap.put("error-invalid-length", ErrorCode.EMAIL_OR_PASSWORD_NOT_CORRECT);
        errorCodeMap.put("error-invalid-email", ErrorCode.EMAIL_OR_PASSWORD_NOT_CORRECT);
    }

    public AppException handleKeyCloakException(FeignException exception) {
        try {
            log.warn("Cannot complete request", exception);
            var response = objectMapper.readValue(exception.contentUTF8(), KeyCloakError.class);

            if (Objects.nonNull(response.getErrorMessage())
                    && Objects.nonNull(errorCodeMap.get(response.getErrorMessage()))) {
                return new AppException(errorCodeMap.get(response.getErrorMessage()));
            }
        } catch (JsonProcessingException e) {
            log.error("Cannot deserialize content", e);
        }

        return new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
    }
}
