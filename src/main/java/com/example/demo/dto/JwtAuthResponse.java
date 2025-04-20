package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с JWT токеном")
public class JwtAuthResponse {

  @Schema(description = "JWT токен для авторизации", example = "eyJhbGciOiJIUzUxMiJ9...")
  private String token;

  @Schema(description = "Тип токена", example = "Bearer")
  private String tokenType = "Bearer";

  @Schema(description = "Идентификатор пользователя", example = "1")
  private Long userId;

  public JwtAuthResponse(String token) {
    this.token = token;
  }

  public JwtAuthResponse(String token, Long userId) {
    this.token = token;
    this.userId = userId;
  }
}