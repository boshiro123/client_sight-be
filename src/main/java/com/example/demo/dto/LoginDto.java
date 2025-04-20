package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Данные для входа пользователя")
public class LoginDto {

  @NotBlank(message = "Email не может быть пустым")
  @Email(message = "Введите корректный email")
  @Schema(description = "Email пользователя", example = "user@example.com", required = true)
  private String email;

  @NotBlank(message = "Пароль не может быть пустым")
  @Schema(description = "Пароль пользователя", example = "password123", required = true)
  private String password;
}