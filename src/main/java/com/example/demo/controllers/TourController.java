package com.example.demo.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.TourDto;
import com.example.demo.service.TourService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tours")
@Tag(name = "Туры", description = "API для работы с турами")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class TourController {

  private final TourService tourService;

  @GetMapping
  @Operation(summary = "Получение списка всех туров", description = "Возвращает список всех доступных туров")
  public ResponseEntity<List<TourDto>> getAllTours() {
    List<TourDto> tours = tourService.getAllTours();
    return ResponseEntity.ok(tours);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получение информации о туре по ID", description = "Возвращает информацию о туре по указанному ID")
  public ResponseEntity<?> getTourById(
      @Parameter(description = "ID тура", required = true) @PathVariable Long id,
      @Parameter(description = "Тип содержимого для возврата (data, image, file)", required = false) @RequestParam(required = false, defaultValue = "data") String contentType) {

    try {
      TourDto tour = tourService.getTourById(id);

      // Возвращаем изображение, если запрошено
      if ("image".equals(contentType)) {
        if (tour.getImageData() == null) {
          return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
            .contentType(MediaType
                .parseMediaType(tour.getImageType() != null ? tour.getImageType() : "application/octet-stream"))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
                (tour.getImageName() != null ? tour.getImageName() : "image.jpg") + "\"")
            .body(tour.getImageData());
      }

      // Возвращаем файл, если запрошен
      if ("file".equals(contentType)) {
        if (tour.getFileData() == null) {
          return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
            .contentType(
                MediaType.parseMediaType(tour.getFileType() != null ? tour.getFileType() : "application/octet-stream"))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
                (tour.getFileName() != null ? tour.getFileName() : "document.pdf") + "\"")
            .body(tour.getFileData());
      }

      // По умолчанию возвращаем данные тура
      return ResponseEntity.ok(tour);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping
  @Operation(summary = "Создание нового тура", description = "Создает новый тур и возвращает информацию о нем", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<TourDto> createTour(
      @Parameter(description = "Данные для создания тура", required = true) @Valid @RequestPart("tour") TourDto tourDto,
      @Parameter(description = "Изображение тура") @RequestPart(value = "image", required = false) MultipartFile imageFile,
      @Parameter(description = "Файл с описанием тура") @RequestPart(value = "file", required = false) MultipartFile descFile) {

    try {
      // Добавляем файлы, если они были переданы
      if (imageFile != null && !imageFile.isEmpty()) {
        tourDto.setImageData(imageFile.getBytes());
        tourDto.setImageName(imageFile.getOriginalFilename());
        tourDto.setImageType(imageFile.getContentType());
      }

      if (descFile != null && !descFile.isEmpty()) {
        tourDto.setFileData(descFile.getBytes());
        tourDto.setFileName(descFile.getOriginalFilename());
        tourDto.setFileType(descFile.getContentType());
      }

      TourDto createdTour = tourService.createTour(tourDto);
      return ResponseEntity.status(HttpStatus.CREATED).body(createdTour);
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PutMapping("/{id}")
  @Operation(summary = "Обновление информации о туре", description = "Обновляет информацию о туре с указанным ID", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<TourDto> updateTour(
      @Parameter(description = "ID тура", required = true) @PathVariable Long id,
      @Parameter(description = "Обновленные данные тура", required = true) @Valid @RequestPart("tour") TourDto tourDto,
      @Parameter(description = "Изображение тура") @RequestPart(value = "image", required = false) MultipartFile imageFile,
      @Parameter(description = "Файл с описанием тура") @RequestPart(value = "file", required = false) MultipartFile descFile) {

    try {
      // Получаем текущие данные тура
      TourDto existingTour = tourService.getTourById(id);

      // Если новые файлы не предоставлены, сохраняем существующие
      if (imageFile == null || imageFile.isEmpty()) {
        tourDto.setImageData(existingTour.getImageData());
        tourDto.setImageName(existingTour.getImageName());
        tourDto.setImageType(existingTour.getImageType());
      } else {
        tourDto.setImageData(imageFile.getBytes());
        tourDto.setImageName(imageFile.getOriginalFilename());
        tourDto.setImageType(imageFile.getContentType());
      }

      if (descFile == null || descFile.isEmpty()) {
        tourDto.setFileData(existingTour.getFileData());
        tourDto.setFileName(existingTour.getFileName());
        tourDto.setFileType(existingTour.getFileType());
      } else {
        tourDto.setFileData(descFile.getBytes());
        tourDto.setFileName(descFile.getOriginalFilename());
        tourDto.setFileType(descFile.getContentType());
      }

      TourDto updatedTour = tourService.updateTour(id, tourDto);
      return ResponseEntity.ok(updatedTour);
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Удаление тура", description = "Удаляет тур с указанным ID", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<Void> deleteTour(
      @Parameter(description = "ID тура", required = true) @PathVariable Long id) {
    tourService.deleteTour(id);
    return ResponseEntity.noContent().build();
  }
}