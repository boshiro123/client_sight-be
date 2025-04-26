package com.example.demo.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApplicationDto;
import com.example.demo.dto.StatusUpdateDto;
import com.example.demo.models.ApplicationStatus;
import com.example.demo.service.ApplicationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/applications")
@Tag(name = "Заявки", description = "API для работы с заявками на туры")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ApplicationController {

  private final ApplicationService applicationService;

  @GetMapping
  @Operation(summary = "Получение списка всех заявок", description = "Возвращает список всех заявок на туры", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<List<ApplicationDto>> getAllApplications() {
    List<ApplicationDto> applications = applicationService.getAllApplications();
    return ResponseEntity.ok(applications);
  }

  @GetMapping("/by-email")
  @Operation(summary = "Получение списка всех заявок по почте", description = "Возвращает список всех заявок на туры по указанной почте", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<List<ApplicationDto>> getAllApplicationsByEmail(
      @Parameter(description = "Почта", required = true) @RequestParam String email) {
    List<ApplicationDto> applications = applicationService.getAllApplicationsByEmail(email);
    return ResponseEntity.ok(applications);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получение информации о заявке по ID", description = "Возвращает информацию о заявке по указанному ID", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<ApplicationDto> getApplicationById(
      @Parameter(description = "ID заявки", required = true) @PathVariable Long id) {
    ApplicationDto application = applicationService.getApplicationById(id);
    return ResponseEntity.ok(application);
  }

  @PostMapping
  @Operation(summary = "Создание новой заявки на тур", description = "Создает новую заявку на тур. Если в системе есть пользователь с указанным email, связывает заявку с ним")
  public ResponseEntity<ApplicationDto> createApplication(
      @Parameter(description = "Данные для создания заявки", required = true) @Valid @RequestBody ApplicationDto applicationDto) {
    ApplicationDto createdApplication = applicationService.createApplication(applicationDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdApplication);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Обновление информации о заявке", description = "Обновляет информацию о заявке с указанным ID", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<ApplicationDto> updateApplication(
      @Parameter(description = "ID заявки", required = true) @PathVariable Long id,
      @Parameter(description = "Обновленные данные заявки", required = true) @Valid @RequestBody ApplicationDto applicationDto) {
    ApplicationDto updatedApplication = applicationService.updateApplication(id, applicationDto);
    return ResponseEntity.ok(updatedApplication);
  }

  @PutMapping("/{id}/status/{status}")
  @Operation(summary = "Обновление статуса заявки", description = "Обновляет статус заявки с указанным ID. При смене статуса на REJECTED, освобождается место в туре и деактивируется связь клиент-тур", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<ApplicationDto> updateApplicationStatus(
      @Parameter(description = "ID заявки", required = true) @PathVariable Long id,
      @Parameter(description = "Новый статус заявки (PENDING, APPROVED, REJECTED, CANCELED)", required = true) @PathVariable String status) {

    try {
      ApplicationStatus newStatus = ApplicationStatus.valueOf(status);
      ApplicationDto updatedApplication = applicationService.updateApplicationStatus(id, newStatus);
      return ResponseEntity.ok(updatedApplication);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "Некорректный статус. Допустимые значения: PENDING, APPROVED, REJECTED, CANCELED");
    }
  }

  @PutMapping("/{id}/status")
  @Operation(summary = "Обновление статуса заявки (альтернативный метод)", description = "Обновляет статус заявки с указанным ID с использованием объекта в теле запроса", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<ApplicationDto> updateApplicationStatusWithBody(
      @Parameter(description = "ID заявки", required = true) @PathVariable Long id,
      @Parameter(description = "Объект, содержащий новый статус заявки", required = true) @RequestBody StatusUpdateDto statusUpdateDto) {

    try {
      ApplicationStatus newStatus = ApplicationStatus.valueOf(statusUpdateDto.getStatus());
      ApplicationDto updatedApplication = applicationService.updateApplicationStatus(id, newStatus);
      return ResponseEntity.ok(updatedApplication);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "Некорректный статус. Допустимые значения: PENDING, APPROVED, REJECTED, CANCELED");
    }
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Удаление заявки", description = "Удаляет заявку с указанным ID", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<Void> deleteApplication(
      @Parameter(description = "ID заявки", required = true) @PathVariable Long id) {
    applicationService.deleteApplication(id);
    return ResponseEntity.noContent().build();
  }

}