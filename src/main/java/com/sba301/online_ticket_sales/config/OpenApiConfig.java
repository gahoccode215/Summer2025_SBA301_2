package com.sba301.online_ticket_sales.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
  @Bean
  public OpenAPI openAPI(
      @Value("${open.api.title}") String title,
      @Value("${open.api.version}") String version,
      @Value("${open.api.description}") String description,
      @Value("${open.api.serverUrl}") String serverUrl,
      @Value("${open.api.serverName}") String serverName,
      @Value("${open.api.license}") String license) {
    return new OpenAPI()
        .info(
            new Info()
                .title(title)
                .version(version)
                .description(description)
                .license(new License().name("API license").url(license)))
        .servers(List.of(new Server().url(serverUrl).description(serverName)))
        .components(
            new Components()
                .addSecuritySchemes(
                    "bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
        .security(List.of(new SecurityRequirement().addList("bearerAuth")));
  }

  @Bean
  public GroupedOpenApi groupedOpenApi() {
    return GroupedOpenApi.builder()
        .group("api-service")
        .packagesToScan("com.sba301.online_ticket_sales.controller")
        .build();
  }
}
