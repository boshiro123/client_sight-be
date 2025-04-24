package com.example.demo.dto;

import com.example.demo.models.AgeGroup;
import com.example.demo.models.Gender;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для контакта")
public class ContactDto {

  @Schema(description = "Уникальный идентификатор контакта", example = "1")
  private Long id;

  @Schema(description = "ФИО контакта", example = "Иванов Иван Иванович")
  private String fullName;

  @Schema(description = "Номер телефона", example = "+79001234567")
  private String phoneNumber;

  @Schema(description = "Email", example = "contact@example.com")
  private String email;

  @Schema(description = "Возрастная группа", example = "AGE_26_35")
  private AgeGroup ageGroup;

  @Schema(description = "Пол", example = "MALE")
  private Gender gender;

  @Schema(description = "Предпочитаемый тип тура", example = "BEACH")
  private String preferredTourType;

  @Schema(description = "Процент скидки", example = "10")
  private Integer discountPercent;

  @Schema(description = "Дополнительная информация")
  private String additionalInfo;

  @Schema(description = "Признак, что контакт является клиентом", example = "true")
  private Boolean isClient;
}