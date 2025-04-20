package com.example.demo.models;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Статусы заявок на туры")
public enum ApplicationStatus {
  PENDING, // В ожидании
  APPROVED, // Одобрена
  REJECTED, // Отклонена
  CANCELLED // Отменена
}