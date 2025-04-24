package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class JacksonConfig {

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();

    // Добавляем поддержку Java 8 Date/Time API (LocalDateTime и т.д.)
    objectMapper.registerModule(new JavaTimeModule());

    // Отключаем сериализацию дат как временных меток
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    // Отключаем ошибку при пустых бинах (решает проблему с Hibernate прокси)
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    return objectMapper;
  }
}