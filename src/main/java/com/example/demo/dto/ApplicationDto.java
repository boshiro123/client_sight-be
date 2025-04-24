package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.demo.models.AgeGroup;
import com.example.demo.models.ApplicationStatus;
import com.example.demo.models.ClientTour;
import com.example.demo.models.Contact;
import com.example.demo.models.Gender;
import com.example.demo.models.Tour;
import com.example.demo.models.User;
import com.example.demo.models.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO заявки на тур")
public class ApplicationDto {

  @Schema(description = "Уникальный идентификатор заявки", example = "1")
  private Long id;

  @NotBlank(message = "ФИО обязательно")
  @Schema(description = "ФИО заявителя", example = "Иванов Иван Иванович", required = true)
  private String fullName;

  @NotBlank(message = "Номер телефона обязателен")
  @Schema(description = "Номер телефона", example = "+79001234567", required = true)
  private String phoneNumber;

  @NotBlank(message = "Email обязателен")
  @Email(message = "Email должен быть корректным")
  @Schema(description = "Email", example = "ivan@example.com", required = true)
  private String email;

  @NotNull(message = "ID тура обязателен")
  @Schema(description = "ID тура", example = "1", required = true)
  private Long tourId;

  @Schema(description = "ID пользователя, если заявка от зарегистрированного пользователя", example = "1")
  private Long userId;

  @Schema(description = "Контакт, связанный с заявкой", example = "1")
  private ContactDto contact;

  @Schema(description = "ID контакта, связанного с заявкой", example = "1")
  private Long contactId;

  @Schema(description = "Пол", example = "MALE")
  private Gender gender;

  @Schema(description = "Возрастная группа", example = "UNDER_18")
  private AgeGroup ageGroup;

  @Schema(description = "Статус заявки", example = "PENDING")
  private ApplicationStatus status = ApplicationStatus.PENDING;

  private Tour tour;

  private ClientTour clientTour;

  @Schema(description = "Дата и время создания", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime createdAt;

  @Schema(description = "Дата и время последнего обновления", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime updatedAt;

}