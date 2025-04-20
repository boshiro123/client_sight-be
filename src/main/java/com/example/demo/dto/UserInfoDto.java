package com.example.demo.dto;

import com.example.demo.models.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о пользователе")
public class UserInfoDto {

  @Schema(description = "Идентификатор пользователя", example = "1")
  private Long id;

  @Schema(description = "Email пользователя", example = "user@example.com")
  private String email;

  @Schema(description = "ФИО пользователя", example = "Иванов Иван Иванович")
  private String fullName;

  @Schema(description = "Роль пользователя", example = "TOURIST")
  private UserRole role;
}