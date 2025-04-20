package com.example.demo.models;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Возрастные группы клиентов")
public enum AgeGroup {
  UNDER_18, // до 18 лет
  AGE_18_20, // 18-20 лет
  AGE_21_25, // 21-25 лет
  AGE_26_35, // 26-35 лет
  AGE_36_50, // 36-50 лет
  OVER_50 // старше 50 лет
}