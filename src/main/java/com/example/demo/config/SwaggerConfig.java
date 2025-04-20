package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Value("${server.servlet.context-path:}")
  private String contextPath;

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("ClientSight API")
            .description("API для управления клиентами и турами")
            .version("1.0.0")
            .contact(new Contact()
                .name("ClientSight")
                .email("info@clientsight.com")
                .url("https://clientsight.com"))
            .license(new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0")))
        .addServersItem(new Server().url("http://localhost:8080" + contextPath).description("Локальный сервер"))
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
        .components(new io.swagger.v3.oas.models.Components()
            .addSecuritySchemes("bearerAuth",
                new SecurityScheme()
                    .name("bearerAuth")
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT токен авторизации. Пример: Bearer eyJhbGciOiJIUzUxMiJ9...")));
  }
}