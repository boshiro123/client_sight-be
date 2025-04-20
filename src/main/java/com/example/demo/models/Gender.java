package com.example.demo.models;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Пол")
public enum Gender {
  MALE, // Мужской
  FEMALE, // Женский
  OTHER // Другой
}