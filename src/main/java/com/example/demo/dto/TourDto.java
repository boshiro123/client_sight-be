package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.models.Season;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
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
@Schema(description = "DTO тура")
public class TourDto {

  @Schema(description = "Уникальный идентификатор тура", example = "1")
  private Long id;

  @NotBlank(message = "Название тура обязательно")
  @Schema(description = "Название тура", example = "Пляжный отдых в Турции", required = true)
  private String name;

  @NotBlank(message = "Описание тура обязательно")
  @Schema(description = "Описание тура", example = "Прекрасный отдых на пляжах Турции с системой 'все включено'", required = true)
  private String description;

  @NotBlank(message = "Страна обязательна")
  @Schema(description = "Страна проведения тура", example = "Турция", required = true)
  private String country;

  @NotNull(message = "Сезон обязателен")
  @Schema(description = "Сезон тура", example = "SUMMER", required = true)
  private Season season;

  @NotBlank(message = "Тип тура обязателен")
  @Schema(description = "Тип тура", example = "BEACH", required = true)
  private String type;

  @Schema(description = "Двоичные данные изображения тура")
  private byte[] imageData;

  @Schema(description = "Имя файла изображения", example = "tour_photo.jpg")
  private String imageName;

  @Schema(description = "MIME-тип изображения", example = "image/jpeg")
  private String imageType;

  @Schema(description = "Город проведения тура", example = "Анталия")
  private String city;

  @Schema(description = "Двоичные данные файла с описанием тура")
  private byte[] fileData;

  @Schema(description = "Имя файла с описанием", example = "tour_description.pdf")
  private String fileName;

  @Schema(description = "MIME-тип файла", example = "application/pdf")
  private String fileType;

  @Min(value = 0, message = "Цена тура не может быть отрицательной")
  @Schema(description = "Цена тура", example = "10000", required = true)
  private Double price;

  @NotNull(message = "Дата начала тура обязательна")
  @Schema(description = "Дата начала тура", example = "2023-06-15T00:00:00", required = true)
  private LocalDateTime startDate;

  @NotNull(message = "Дата окончания тура обязательна")
  @Schema(description = "Дата окончания тура", example = "2023-06-25T00:00:00", required = true)
  private LocalDateTime endDate;

  @Min(value = 1, message = "Общее количество мест должно быть положительным")
  @Schema(description = "Общее количество мест", example = "30", required = true)
  private Integer totalSlots;

  @Min(value = 0, message = "Количество свободных мест не может быть отрицательным")
  @Schema(description = "Количество свободных мест", example = "15", required = true)
  private Integer availableSlots;

  @Schema(description = "Признак закрытия регистрации", example = "false")
  private Boolean isRegistrationClosed = false;
}