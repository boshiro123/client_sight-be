package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins("http://localhost:4040", "http://207.180.212.53:4040")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true)
        .maxAge(3600);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // Добавляем только необходимые собственные статические ресурсы
    registry.addResourceHandler("/static/**")
        .addResourceLocations("classpath:/static/");

    // Отключаем стандартные обработчики WebJars, которые вызывают ошибку
    // Это предотвратит автоматическую регистрацию WebJarsResourceResolver
  }
}