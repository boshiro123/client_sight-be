package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.TourDto;
import com.example.demo.models.Tour;
import com.example.demo.repository.TourRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TourService {

  private final TourRepository tourRepository;

  /**
   * Получение списка всех туров
   */
  @Transactional(readOnly = true)
  public List<TourDto> getAllTours() {
    return tourRepository.findAll().stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  /**
   * Получение тура по идентификатору
   */
  @Transactional(readOnly = true)
  public TourDto getTourById(Long id) {
    Tour tour = tourRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Тур с ID " + id + " не найден"));
    return convertToDto(tour);
  }

  /**
   * Создание нового тура
   */
  @Transactional
  public TourDto createTour(TourDto tourDto) {
    Tour tour = convertToEntity(tourDto);
    Tour savedTour = tourRepository.save(tour);
    return convertToDto(savedTour);
  }

  /**
   * Обновление существующего тура
   */
  @Transactional
  public TourDto updateTour(Long id, TourDto tourDto) {
    if (!tourRepository.existsById(id)) {
      throw new EntityNotFoundException("Тур с ID " + id + " не найден");
    }

    Tour tour = convertToEntity(tourDto);
    tour.setId(id);
    Tour updatedTour = tourRepository.save(tour);
    return convertToDto(updatedTour);
  }

  /**
   * Удаление тура
   */
  @Transactional
  public void deleteTour(Long id) {
    if (!tourRepository.existsById(id)) {
      throw new EntityNotFoundException("Тур с ID " + id + " не найден");
    }
    tourRepository.deleteById(id);
  }

  /**
   * Конвертация Entity в DTO
   */
  private TourDto convertToDto(Tour tour) {
    return TourDto.builder()
        .id(tour.getId())
        .name(tour.getName())
        .description(tour.getDescription())
        .country(tour.getCountry())
        .season(tour.getSeason())
        .type(tour.getType())
        .imageData(tour.getImageData())
        .imageName(tour.getImageName())
        .imageType(tour.getImageType())
        .fileData(tour.getFileData())
        .fileName(tour.getFileName())
        .fileType(tour.getFileType())
        .startDate(tour.getStartDate())
        .endDate(tour.getEndDate())
        .totalSlots(tour.getTotalSlots())
        .availableSlots(tour.getAvailableSlots())
        .isRegistrationClosed(tour.getIsRegistrationClosed())
        .build();
  }

  /**
   * Конвертация DTO в Entity
   */
  private Tour convertToEntity(TourDto tourDto) {
    return Tour.builder()
        .id(tourDto.getId())
        .name(tourDto.getName())
        .description(tourDto.getDescription())
        .country(tourDto.getCountry())
        .season(tourDto.getSeason())
        .type(tourDto.getType())
        .imageData(tourDto.getImageData())
        .imageName(tourDto.getImageName())
        .imageType(tourDto.getImageType())
        .fileData(tourDto.getFileData())
        .fileName(tourDto.getFileName())
        .fileType(tourDto.getFileType())
        .startDate(tourDto.getStartDate())
        .endDate(tourDto.getEndDate())
        .totalSlots(tourDto.getTotalSlots())
        .availableSlots(tourDto.getAvailableSlots())
        .isRegistrationClosed(tourDto.getIsRegistrationClosed())
        .build();
  }
}