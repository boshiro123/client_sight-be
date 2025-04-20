package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Данные для регистрации пользователя")
public class RegisterDto {

  @NotBlank(message = "ФИО не может быть пустым")
  @Size(min = 3, max = 100, message = "ФИО должно быть от 3 до 100 символов")
  @Schema(description = "ФИО пользователя", example = "Иванов Иван Иванович", required = true)
  private String fullName;

  @NotBlank(message = "Email не может быть пустым")
  @Email(message = "Введите корректный email")
  @Schema(description = "Email пользователя", example = "user@example.com", required = true)
  private String email;

  @NotBlank(message = "Пароль не может быть пустым")
  @Size(min = 3, message = "Пароль должен быть не менее 6 символов")
  @Schema(description = "Пароль пользователя", example = "password123", required = true)
  private String password;
}