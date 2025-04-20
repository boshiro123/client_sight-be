package com.example.demo.models;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Роли пользователей в системе")
public enum UserRole {
  TOURIST, // Турист
  EMPLOYEE, // Сотрудник
  MANAGER // Управляющий
}