package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.ApplicationDto;
import com.example.demo.dto.ContactDto;
import com.example.demo.models.Application;
import com.example.demo.models.ApplicationStatus;
import com.example.demo.models.ClientTour;
import com.example.demo.models.Contact;
import com.example.demo.models.Tour;
import com.example.demo.models.User;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.ClientTourRepository;
import com.example.demo.repository.ContactRepository;
import com.example.demo.repository.TourRepository;
import com.example.demo.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationService {

  private final ApplicationRepository applicationRepository;
  private final TourRepository tourRepository;
  private final UserRepository userRepository;
  private final ContactRepository contactRepository;
  private final ClientTourRepository clientTourRepository;

  /**
   * Получение списка всех заявок
   */
  @Transactional(readOnly = true)
  public List<ApplicationDto> getAllApplications() {
    return applicationRepository.findAllWithDetails().stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  /**
   * Получение списка всех заявок по почте
   */
  @Transactional(readOnly = true)
  public List<ApplicationDto> getAllApplicationsByEmail(String email) {
    return applicationRepository.findAllByEmail(email).stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  /**
   * Получение заявки по идентификатору
   */
  @Transactional(readOnly = true)
  public ApplicationDto getApplicationById(Long id) {
    Application application = applicationRepository.findByIdWithDetails(id)
        .orElseThrow(() -> new EntityNotFoundException("Заявка с ID " + id + " не найдена"));
    return convertToDto(application);
  }

  /**
   * Создание новой заявки на тур
   * - Проверяет существует ли пользователь с указанным email
   * - Создает запись в Contact, если еще нет контакта с такой почтой
   * - Уменьшает количество доступных мест в туре на 1
   * - Создает связь между контактом и туром в ClientTour
   */
  @Transactional
  public ApplicationDto createApplication(ApplicationDto applicationDto) {
    // Проверяем существование тура и получаем его
    Tour tour = tourRepository.findById(applicationDto.getTourId())
        .orElseThrow(() -> new EntityNotFoundException("Тур с ID " + applicationDto.getTourId() + " не найден"));

    // Проверяем доступность мест в туре
    if (tour.getAvailableSlots() <= 0) {
      throw new IllegalStateException("В туре нет доступных мест");
    }

    // Уменьшаем количество доступных мест в туре на 1
    tour.setAvailableSlots(tour.getAvailableSlots() - 1);
    tourRepository.save(tour);

    // Проверяем существование пользователя с указанным email
    User user = userRepository.findByEmail(applicationDto.getEmail()).orElse(null);
    if (user != null) {
      applicationDto.setUserId(user.getId());
    } else {
      applicationDto.setUserId(null);
    }

    // Проверяем существование контакта с указанным email
    Contact contact = contactRepository.findByEmail(applicationDto.getEmail()).orElse(null);

    // Если контакта нет, создаем новый
    if (contact == null) {
      contact = Contact.builder()
          .fullName(applicationDto.getFullName())
          .phoneNumber(applicationDto.getPhoneNumber())
          .email(applicationDto.getEmail())
          .ageGroup(applicationDto.getAgeGroup())
          .gender(applicationDto.getGender())
          .discountPercent(0)
          .isClient(false) // Делаем клиентом сразу, так как подал заявку
          .user(user) // Связываем с пользователем, если он есть
          .build();

      contact = contactRepository.save(contact);
    }

    // Создаем заявку
    Application application = convertToEntity(applicationDto);
    application.setContactId(contact.getId());
    application.setContact(contact);
    Application savedApplication = applicationRepository.save(application);

    // Создаем связь между контактом и туром
    ClientTour clientTour = ClientTour.builder()
        .contactId(contact.getId())
        .tourId(tour.getId())
        .applicationId(savedApplication.getId())
        .isActive(true)
        .build();

    clientTourRepository.save(clientTour);

    return convertToDto(savedApplication);
  }

  /**
   * Обновление существующей заявки
   */
  @Transactional
  public ApplicationDto updateApplication(Long id, ApplicationDto applicationDto) {
    if (!applicationRepository.existsById(id)) {
      throw new EntityNotFoundException("Заявка с ID " + id + " не найдена");
    }

    Application application = convertToEntity(applicationDto);
    application.setId(id);
    Application updatedApplication = applicationRepository.save(application);
    return convertToDto(updatedApplication);
  }

  /**
   * Удаление заявки
   * При удалении заявки:
   * - Освобождается место в туре (увеличивается availableSlots)
   * - Удаляется связь клиент-тур, если она была создана
   */
  @Transactional
  public void deleteApplication(Long id) {
    Application application = applicationRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Заявка с ID " + id + " не найдена"));

    // Получаем тур и увеличиваем количество доступных мест
    Tour tour = tourRepository.findById(application.getTourId())
        .orElseThrow(() -> new EntityNotFoundException("Тур с ID " + application.getTourId() + " не найден"));

    tour.setAvailableSlots(tour.getAvailableSlots() + 1);
    tourRepository.save(tour);

    // Находим и удаляем связь в ClientTour, если она была создана по этой заявке
    clientTourRepository.findByApplicationId(application.getId())
        .ifPresent(clientTourRepository::delete);

    // Удаляем саму заявку
    applicationRepository.deleteById(id);
  }

  /**
   * Обновление статуса заявки
   */
  @Transactional
  public ApplicationDto updateApplicationStatus(Long id, ApplicationStatus status) {
    Application application = applicationRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Заявка с ID " + id + " не найдена"));

    // Сохраняем предыдущий статус для логики ниже
    ApplicationStatus previousStatus = application.getStatus();

    // Обновляем статус
    application.setStatus(status);

    // Если заявка была отклонена (REJECTED) и раньше была в другом статусе,
    // нужно вернуть место в туре и деактивировать связь клиент-тур
    if ((status == ApplicationStatus.REJECTED && previousStatus != ApplicationStatus.REJECTED)
        || (status == ApplicationStatus.CANCELLED)) {
      // Получаем тур и увеличиваем количество доступных мест
      Tour tour = tourRepository.findById(application.getTourId())
          .orElseThrow(() -> new EntityNotFoundException("Тур с ID " + application.getTourId() + " не найден"));

      tour.setAvailableSlots(tour.getAvailableSlots() + 1);
      tourRepository.save(tour);

      // Находим и деактивируем связь в ClientTour, если она была создана по этой
      // заявке
      clientTourRepository.findByApplicationId(application.getId())
          .ifPresent(clientTour -> {
            clientTour.setIsActive(false);
            clientTourRepository.save(clientTour);
          });
    }

    // Если заявка была восстановлена из отклоненного статуса в APPROVED или
    // PENDING,
    // нужно занять место в туре и активировать связь клиент-тур
    if ((status == ApplicationStatus.APPROVED || status == ApplicationStatus.PENDING)
        && previousStatus == ApplicationStatus.REJECTED) {
      // Получаем тур и проверяем наличие мест
      Tour tour = tourRepository.findById(application.getTourId())
          .orElseThrow(() -> new EntityNotFoundException("Тур с ID " + application.getTourId() + " не найден"));

      if (tour.getAvailableSlots() <= 0) {
        throw new IllegalStateException("В туре нет доступных мест для изменения статуса заявки");
      }

      // Уменьшаем количество доступных мест
      tour.setAvailableSlots(tour.getAvailableSlots() - 1);
      tourRepository.save(tour);

      // Находим и активируем связь в ClientTour, если она была создана по этой заявке
      clientTourRepository.findByApplicationId(application.getId())
          .ifPresent(clientTour -> {
            clientTour.setIsActive(true);
            clientTourRepository.save(clientTour);
          });
    }

    Application updatedApplication = applicationRepository.save(application);
    return convertToDto(updatedApplication);
  }

  /**
   * Конвертация Entity в DTO
   */
  private ApplicationDto convertToDto(Application application) {
    ApplicationDto dto = ApplicationDto.builder()
        .id(application.getId())
        .fullName(application.getFullName())
        .phoneNumber(application.getPhoneNumber())
        .email(application.getEmail())
        .tourId(application.getTourId())
        .userId(application.getUserId())
        .status(application.getStatus())
        .contact(contactToDto(application.getContact()))
        .createdAt(application.getCreatedAt())
        .updatedAt(application.getUpdatedAt())
        .build();

    // Устанавливаем связанные сущности
    if (application.getTour() != null) {
      dto.setTour(application.getTour());
    }

    if (application.getClientTour() != null) {
      dto.setClientTour(application.getClientTour());
    }

    return dto;
  }

  /**
   * Конвертация DTO в Entity
   */
  private Application convertToEntity(ApplicationDto applicationDto) {
    return Application.builder()
        .id(applicationDto.getId())
        .fullName(applicationDto.getFullName())
        .phoneNumber(applicationDto.getPhoneNumber())
        .email(applicationDto.getEmail())
        .tourId(applicationDto.getTourId())
        .userId(applicationDto.getUserId())
        .contactId(applicationDto.getContactId())
        .status(applicationDto.getStatus())
        .build();
  }

  private ContactDto contactToDto(Contact contact) {
    return ContactDto.builder()
        .id(contact.getId())
        .fullName(contact.getFullName())
        .phoneNumber(contact.getPhoneNumber())
        .email(contact.getEmail())
        .ageGroup(contact.getAgeGroup())
        .gender(contact.getGender())
        .discountPercent(contact.getDiscountPercent())
        .isClient(contact.getIsClient())
        .build();
  }
}