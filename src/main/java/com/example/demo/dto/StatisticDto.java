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
@Schema(description = "Статистика для сотрудника")
public class StatisticDto {

  @Schema(description = "Количество туров")
  private Long tourCount;

  @Schema(description = "Количество заявок")
  private Long applicationCount;

  @Schema(description = "Количество клиентов")
  private Long clientCount;

}