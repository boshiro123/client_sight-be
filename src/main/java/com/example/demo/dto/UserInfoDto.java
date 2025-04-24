package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.models.Contact;
import com.example.demo.models.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о пользователе")
public class UserInfoDto {

  @Schema(description = "Уникальный идентификатор пользователя", example = "1")
  private Long id;

  @Schema(description = "ФИО пользователя", example = "Иванов Иван Иванович", required = true)
  private String fullName;

  @Schema(description = "Email пользователя", example = "user@example.com", required = true)
  private String email;

  @Schema(description = "Роль пользователя", example = "TOURIST", required = true)
  private UserRole role;

  @Schema(description = "Дата и время создания", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime createdAt;

  @Schema(description = "Дата и время последнего обновления", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime updatedAt;

  private Contact contact;

}