package com.example.demo.models;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Сезоны года")
public enum Season {
  WINTER, // Зима
  SPRING, // Весна
  SUMMER, // Лето
  AUTUMN // Осень
}