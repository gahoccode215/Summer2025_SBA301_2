package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.dto.identity.TokenExchangeParam;
import com.sba301.online_ticket_sales.dto.identity.TokenExchangeResponse;
import com.sba301.online_ticket_sales.dto.identity.UserCreationParam;
import com.sba301.online_ticket_sales.dto.auth.response.LoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "identity-client", url = "${idp.url}")
public interface IdentityClient {
    @PostMapping(value = "/realms/${idp.realm}/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//    TokenExchangeResponse exchangeToken(@QueryMap TokenExchangeParam param);
    TokenExchangeResponse exchangeToken(@RequestBody TokenExchangeParam param);

    @PostMapping(value = "/admin/realms/sba301/users",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createUser(
            @RequestHeader("Authorization") String token,
            @RequestBody UserCreationParam param);

    @PostMapping(value = "/realms/${idp.realm}/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    LoginResponse login(@RequestBody TokenExchangeParam param);
}