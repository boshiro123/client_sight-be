package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.ClientTourDto;
import com.example.demo.models.ClientTour;
import com.example.demo.models.Contact;
import com.example.demo.models.Tour;
import com.example.demo.repository.ClientTourRepository;
import com.example.demo.repository.ContactRepository;
import com.example.demo.repository.TourRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientTourService {

  private final ClientTourRepository clientTourRepository;
  private final ContactRepository contactRepository;
  private final TourRepository tourRepository;

  /**
   * Получение списка всех связей клиент-тур
   */
  @Transactional(readOnly = true)
  public List<ClientTourDto> getAllClientTours() {
    return clientTourRepository.findAll().stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  /**
   * Получение списка всех активных связей клиент-тур с деталями
   */
  @Transactional(readOnly = true)
  public List<ClientTourDto> getAllActiveClientToursWithDetails() {
    return clientTourRepository.findAllActiveWithDetails().stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  /**
   * Получение связи клиент-тур по идентификатору
   */
  @Transactional(readOnly = true)
  public ClientTourDto getClientTourById(Long id) {
    ClientTour clientTour = clientTourRepository.findByIdWithDetails(id)
        .orElseThrow(() -> new EntityNotFoundException("Связь клиент-тур с ID " + id + " не найдена"));
    return convertToDto(clientTour);
  }

  /**
   * Получение всех связей для клиента
   */
  @Transactional(readOnly = true)
  public List<ClientTourDto> getClientToursByContactId(Long contactId) {
    if (!contactRepository.existsById(contactId)) {
      throw new EntityNotFoundException("Контакт с ID " + contactId + " не найден");
    }
    return clientTourRepository.findByContactId(contactId).stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  /**
   * Получение всех связей для тура
   */
  @Transactional(readOnly = true)
  public List<ClientTourDto> getClientToursByTourId(Long tourId) {
    if (!tourRepository.existsById(tourId)) {
      throw new EntityNotFoundException("Тур с ID " + tourId + " не найден");
    }
    return clientTourRepository.findByTourId(tourId).stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  /**
   * Создание новой связи клиент-тур
   */
  @Transactional
  public ClientTourDto createClientTour(ClientTourDto clientTourDto) {
    // Проверяем существование контакта
    Contact contact = contactRepository.findById(clientTourDto.getContactId())
        .orElseThrow(() -> new EntityNotFoundException("Контакт с ID " + clientTourDto.getContactId() + " не найден"));

    // Проверяем существование тура
    Tour tour = tourRepository.findById(clientTourDto.getTourId())
        .orElseThrow(() -> new EntityNotFoundException("Тур с ID " + clientTourDto.getTourId() + " не найден"));

    // Проверяем, что такой связи еще нет
    if (clientTourRepository.findByContactIdAndTourId(clientTourDto.getContactId(), clientTourDto.getTourId())
        .isPresent()) {
      throw new IllegalStateException("Связь между контактом ID " + clientTourDto.getContactId() +
          " и туром ID " + clientTourDto.getTourId() + " уже существует");
    }

    // Проверяем доступность мест в туре
    if (tour.getAvailableSlots() <= 0) {
      throw new IllegalStateException("В туре нет доступных мест");
    }

    // Уменьшаем количество доступных мест в туре
    tour.setAvailableSlots(tour.getAvailableSlots() - 1);
    tourRepository.save(tour);

    // Создаем связь
    ClientTour clientTour = convertToEntity(clientTourDto);
    ClientTour savedClientTour = clientTourRepository.save(clientTour);

    return convertToDto(savedClientTour);
  }

  /**
   * Обновление существующей связи клиент-тур
   */
  @Transactional
  public ClientTourDto updateClientTour(Long id, ClientTourDto clientTourDto) {
    ClientTour existingClientTour = clientTourRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Связь клиент-тур с ID " + id + " не найдена"));

    // Проверяем, не изменился ли тур, если да - обновляем доступные места
    if (!existingClientTour.getTourId().equals(clientTourDto.getTourId()) &&
        clientTourDto.getTourId() != null) {

      // Возвращаем место в старом туре
      Tour oldTour = tourRepository.findById(existingClientTour.getTourId())
          .orElseThrow(() -> new EntityNotFoundException("Тур с ID " + existingClientTour.getTourId() + " не найден"));
      oldTour.setAvailableSlots(oldTour.getAvailableSlots() + 1);
      tourRepository.save(oldTour);

      // Занимаем место в новом туре
      Tour newTour = tourRepository.findById(clientTourDto.getTourId())
          .orElseThrow(() -> new EntityNotFoundException("Тур с ID " + clientTourDto.getTourId() + " не найден"));

      if (newTour.getAvailableSlots() <= 0) {
        throw new IllegalStateException("В новом туре нет доступных мест");
      }

      newTour.setAvailableSlots(newTour.getAvailableSlots() - 1);
      tourRepository.save(newTour);
    }

    // Обновляем только разрешенные поля
    if (clientTourDto.getContactId() != null) {
      existingClientTour.setContactId(clientTourDto.getContactId());
    }

    if (clientTourDto.getTourId() != null) {
      existingClientTour.setTourId(clientTourDto.getTourId());
    }

    if (clientTourDto.getApplicationId() != null) {
      existingClientTour.setApplicationId(clientTourDto.getApplicationId());
    }

    if (clientTourDto.getIsActive() != null) {
      existingClientTour.setIsActive(clientTourDto.getIsActive());
    }

    ClientTour updatedClientTour = clientTourRepository.save(existingClientTour);
    return convertToDto(updatedClientTour);
  }

  /**
   * Удаление связи клиент-тур
   */
  @Transactional
  public void deleteClientTour(Long id) {
    ClientTour clientTour = clientTourRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Связь клиент-тур с ID " + id + " не найдена"));

    // Возвращаем место в туре
    Tour tour = tourRepository.findById(clientTour.getTourId())
        .orElseThrow(() -> new EntityNotFoundException("Тур с ID " + clientTour.getTourId() + " не найден"));

    tour.setAvailableSlots(tour.getAvailableSlots() + 1);
    tourRepository.save(tour);

    // Удаляем связь
    clientTourRepository.deleteById(id);
  }

  /**
   * Установка статуса активности для связи клиент-тур
   */
  @Transactional
  public ClientTourDto setActiveStatus(Long id, Boolean isActive) {
    ClientTour clientTour = clientTourRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Связь клиент-тур с ID " + id + " не найдена"));

    // Если меняем активность с false на true, проверяем наличие мест
    if (isActive && !clientTour.getIsActive()) {
      Tour tour = tourRepository.findById(clientTour.getTourId())
          .orElseThrow(() -> new EntityNotFoundException("Тур с ID " + clientTour.getTourId() + " не найден"));

      if (tour.getAvailableSlots() <= 0) {
        throw new IllegalStateException("В туре нет доступных мест для активации связи");
      }

      // Занимаем место в туре
      tour.setAvailableSlots(tour.getAvailableSlots() - 1);
      tourRepository.save(tour);
    }

    // Если меняем активность с true на false, освобождаем место
    if (!isActive && clientTour.getIsActive()) {
      Tour tour = tourRepository.findById(clientTour.getTourId())
          .orElseThrow(() -> new EntityNotFoundException("Тур с ID " + clientTour.getTourId() + " не найден"));

      tour.setAvailableSlots(tour.getAvailableSlots() + 1);
      tourRepository.save(tour);
    }

    clientTour.setIsActive(isActive);
    ClientTour updatedClientTour = clientTourRepository.save(clientTour);
    return convertToDto(updatedClientTour);
  }

  /**
   * Конвертация Entity в DTO
   */
  private ClientTourDto convertToDto(ClientTour clientTour) {
    ClientTourDto dto = ClientTourDto.builder()
        .id(clientTour.getId())
        .contactId(clientTour.getContactId())
        .tourId(clientTour.getTourId())
        .applicationId(clientTour.getApplicationId())
        .isActive(clientTour.getIsActive())
        .createdAt(clientTour.getCreatedAt())
        .updatedAt(clientTour.getUpdatedAt())
        .build();

    // Добавляем информацию о клиенте и туре, если они загружены
    if (clientTour.getContact() != null) {
      dto.setContactFullName(clientTour.getContact().getFullName());
    }

    if (clientTour.getTour() != null) {
      dto.setTourName(clientTour.getTour().getName());
    }

    return dto;
  }

  /**
   * Конвертация DTO в Entity
   */
  private ClientTour convertToEntity(ClientTourDto clientTourDto) {
    return ClientTour.builder()
        .id(clientTourDto.getId())
        .contactId(clientTourDto.getContactId())
        .tourId(clientTourDto.getTourId())
        .applicationId(clientTourDto.getApplicationId())
        .isActive(clientTourDto.getIsActive() != null ? clientTourDto.getIsActive() : true)
        .build();
  }
}