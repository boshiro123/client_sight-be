package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO связи клиента с туром")
public class ClientTourDto {

  @Schema(description = "Уникальный идентификатор связи клиент-тур", example = "1")
  private Long id;

  @NotNull(message = "ID контакта обязателен")
  @Schema(description = "ID контакта клиента", example = "1", required = true)
  private Long contactId;

  @NotNull(message = "ID тура обязателен")
  @Schema(description = "ID тура", example = "1", required = true)
  private Long tourId;

  @Schema(description = "ID заявки, если клиент был добавлен через заявку", example = "1")
  private Long applicationId;

  @Schema(description = "Признак активности записи", example = "true")
  private Boolean isActive = true;

  @Schema(description = "Дата и время создания", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime createdAt;

  @Schema(description = "Дата и время последнего обновления", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime updatedAt;

  // Дополнительные поля для отображения связанных данных без запроса к базе
  @Schema(description = "Имя контакта (клиента)")
  private String contactFullName;

  @Schema(description = "Название тура")
  private String tourName;
}