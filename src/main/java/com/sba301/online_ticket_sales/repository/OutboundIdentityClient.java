package com.sba301.online_ticket_sales.repository;

import com.sba301.online_ticket_sales.dto.auth.request.ExchangeTokenRequest;
import com.sba301.online_ticket_sales.dto.auth.response.ExchangeTokenResponse;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "outbound-identity", url = "https://oauth2.googleapis.com")
public interface OutboundIdentityClient {
  @PostMapping(value = "/token", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  ExchangeTokenResponse exchangeToken(@QueryMap ExchangeTokenRequest request);
}
