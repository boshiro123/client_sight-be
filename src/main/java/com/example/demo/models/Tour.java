package com.example.demo.models;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tours")
@Schema(description = "Модель тура")
public class Tour {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "Уникальный идентификатор тура", example = "1")
  private Long id;

  @NotBlank(message = "Название тура обязательно")
  @Column(nullable = false)
  @Schema(description = "Название тура", example = "Пляжный отдых в Турции", required = true)
  private String name;

  @NotBlank(message = "Описание тура обязательно")
  @Column(nullable = false, columnDefinition = "TEXT")
  @Schema(description = "Описание тура", example = "Прекрасный отдых на пляжах Турции с системой 'все включено'", required = true)
  private String description;

  @NotBlank(message = "Страна обязательна")
  @Column(nullable = false)
  @Schema(description = "Страна проведения тура", example = "Турция", required = true)
  private String country;

  @NotNull(message = "Сезон обязателен")
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Schema(description = "Сезон тура", example = "SUMMER", required = true)
  private Season season;

  @NotBlank(message = "Тип тура обязателен")
  @Column(nullable = false)
  @Schema(description = "Тип тура", example = "BEACH", required = true)
  private String type;

  @Column(name = "image_path")
  @Schema(description = "Путь к изображению тура", example = "/uploads/images/tour1.jpg")
  private String imagePath;

  @Column(name = "file_path")
  @Schema(description = "Путь к файлу с описанием тура", example = "/uploads/files/tour1.pdf")
  private String filePath;

  @NotNull(message = "Дата начала тура обязательна")
  @Column(name = "start_date", nullable = false)
  @Schema(description = "Дата начала тура", example = "2023-06-15T00:00:00", required = true)
  private LocalDateTime startDate;

  @NotNull(message = "Дата окончания тура обязательна")
  @Column(name = "end_date", nullable = false)
  @Schema(description = "Дата окончания тура", example = "2023-06-25T00:00:00", required = true)
  private LocalDateTime endDate;

  @Min(value = 0, message = "Продолжительность тура не может быть отрицательной")
  @Column(nullable = false)
  @Schema(description = "Продолжительность тура в днях", example = "10")
  private Integer duration = 0;

  @Min(value = 1, message = "Общее количество мест должно быть положительным")
  @Column(name = "total_slots", nullable = false)
  @Schema(description = "Общее количество мест", example = "30", required = true)
  private Integer totalSlots;

  @Min(value = 0, message = "Количество свободных мест не может быть отрицательным")
  @Column(name = "available_slots", nullable = false)
  @Schema(description = "Количество свободных мест", example = "15", required = true)
  private Integer availableSlots;

  @Column(name = "is_registration_closed", nullable = false)
  @Schema(description = "Признак закрытия регистрации", example = "false")
  private Boolean isRegistrationClosed = false;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  @Schema(description = "Дата и время создания", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  @Schema(description = "Дата и время последнего обновления", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonIgnore
  private List<Application> applications;

  @ManyToMany(mappedBy = "tours")
  @JsonIgnore
  private List<Contact> contacts;

  @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonIgnore
  private List<ClientTour> clientTours;

  @PrePersist
  @PreUpdate
  private void calculateDuration() {
    if (startDate != null && endDate != null) {
      // Вычисление продолжительности в днях
      duration = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate()) + 1;
    }
  }

  // Метод для проверки доступности тура для записи
  public boolean isAvailable() {
    LocalDateTime today = LocalDateTime.now();
    LocalDateTime dayBeforeStart = startDate.minusDays(1);

    return !isRegistrationClosed && availableSlots > 0 && !today.isAfter(dayBeforeStart);
  }
}