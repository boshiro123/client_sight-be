package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.demo.models.ApplicationStatus;
import com.example.demo.models.ClientTour;
import com.example.demo.models.Tour;
import com.example.demo.models.User;
import com.example.demo.models.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о заявке")
public class ApplicationDto {

  @Schema(description = "Уникальный идентификатор заявки", example = "1")
  private Long id;

  @Schema(description = "ФИО заявителя", example = "Иванов Иван Иванович", required = true)
  private String fullName;

  @Schema(description = "Номер телефона", example = "+79001234567", required = true)
  private String phoneNumber;

  @Schema(description = "Email", example = "ivan@example.com", required = true)
  private String email;

  @Schema(description = "ID тура", example = "1", required = true)
  private Long tourId;

  @Schema(description = "ID пользователя, если заявка от зарегистрированного пользователя", example = "1")
  private Long userId;

  @Schema(description = "Статус заявки", example = "PENDING", required = true)
  private ApplicationStatus status = ApplicationStatus.PENDING;

  private Tour tour;

  private ClientTour clientTour;

  @Schema(description = "Дата и время создания", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime createdAt;

  @Schema(description = "Дата и время последнего обновления", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime updatedAt;

}