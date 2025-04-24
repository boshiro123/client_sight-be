package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для обновления статуса заявки")
public class StatusUpdateDto {

  @Schema(description = "Новый статус заявки (PENDING, APPROVED, REJECTED, CANCELED)", example = "APPROVED")
  private String status;
}