package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ClientTourDto;
import com.example.demo.service.ClientTourService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/client-tours")
@RequiredArgsConstructor
@Tag(name = "ClientTour", description = "API для управления связями клиентов с турами")
public class ClientTourController {

  private final ClientTourService clientTourService;

  @GetMapping
  @Operation(summary = "Получить все связи клиент-тур")
  public ResponseEntity<List<ClientTourDto>> getAllClientTours() {
    return ResponseEntity.ok(clientTourService.getAllClientTours());
  }

  @GetMapping("/active")
  @Operation(summary = "Получить все активные связи клиент-тур с деталями")
  public ResponseEntity<List<ClientTourDto>> getAllActiveClientToursWithDetails() {
    return ResponseEntity.ok(clientTourService.getAllActiveClientToursWithDetails());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получить связь клиент-тур по ID")
  public ResponseEntity<ClientTourDto> getClientTourById(
      @Parameter(description = "ID связи клиент-тур") @PathVariable Long id) {
    return ResponseEntity.ok(clientTourService.getClientTourById(id));
  }

  @GetMapping("/contact/{contactId}")
  @Operation(summary = "Получить все связи для клиента")
  public ResponseEntity<List<ClientTourDto>> getClientToursByContactId(
      @Parameter(description = "ID контакта") @PathVariable Long contactId) {
    return ResponseEntity.ok(clientTourService.getClientToursByContactId(contactId));
  }

  @GetMapping("/tour/{tourId}")
  @Operation(summary = "Получить все связи для тура")
  public ResponseEntity<List<ClientTourDto>> getClientToursByTourId(
      @Parameter(description = "ID тура") @PathVariable Long tourId) {
    return ResponseEntity.ok(clientTourService.getClientToursByTourId(tourId));
  }

  @PostMapping
  @Operation(summary = "Создать новую связь клиент-тур")
  public ResponseEntity<ClientTourDto> createClientTour(
      @Parameter(description = "Данные для создания связи клиент-тур") @Valid @RequestBody ClientTourDto clientTourDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(clientTourService.createClientTour(clientTourDto));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Обновить существующую связь клиент-тур")
  public ResponseEntity<ClientTourDto> updateClientTour(
      @Parameter(description = "ID связи клиент-тур") @PathVariable Long id,
      @Parameter(description = "Данные для обновления связи клиент-тур") @Valid @RequestBody ClientTourDto clientTourDto) {
    return ResponseEntity.ok(clientTourService.updateClientTour(id, clientTourDto));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Удалить связь клиент-тур")
  public ResponseEntity<Void> deleteClientTour(
      @Parameter(description = "ID связи клиент-тур") @PathVariable Long id) {
    clientTourService.deleteClientTour(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}/active/{status}")
  @Operation(summary = "Установить статус активности для связи клиент-тур")
  public ResponseEntity<ClientTourDto> setActiveStatus(
      @Parameter(description = "ID связи клиент-тур") @PathVariable Long id,
      @Parameter(description = "Статус активности (true/false)") @PathVariable Boolean status) {
    return ResponseEntity.ok(clientTourService.setActiveStatus(id, status));
  }
}