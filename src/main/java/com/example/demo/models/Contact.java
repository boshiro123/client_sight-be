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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
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
@Table(name = "contacts")
@Schema(description = "Модель контакта")
public class Contact {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "Уникальный идентификатор контакта", example = "1")
  private Long id;

  @NotBlank(message = "ФИО обязательно")
  @Column(name = "full_name", nullable = false)
  @Schema(description = "ФИО контакта", example = "Иванов Иван Иванович", required = true)
  private String fullName;

  @NotBlank(message = "Номер телефона обязателен")
  @Column(name = "phone_number", nullable = false)
  @Schema(description = "Номер телефона", example = "+79001234567", required = true)
  private String phoneNumber;

  @NotBlank(message = "Email обязателен")
  @Email(message = "Email должен быть корректным")
  @Column(nullable = false)
  @Schema(description = "Email", example = "contact@example.com", required = true)
  private String email;

  @NotNull(message = "Возрастная группа обязательна")
  @Enumerated(EnumType.STRING)
  @Column(name = "age_group", nullable = false)
  @Schema(description = "Возрастная группа", example = "AGE_26_35", required = true)
  private AgeGroup ageGroup;

  @NotNull(message = "Пол обязателен")
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Schema(description = "Пол", example = "MALE", required = true)
  private Gender gender;

  @Column(name = "preferred_tour_type")
  @Schema(description = "Предпочитаемый тип тура", example = "BEACH")
  private String preferredTourType;

  @Min(value = 0, message = "Процент скидки не может быть отрицательным")
  @Max(value = 100, message = "Процент скидки не может быть больше 100")
  @Column(name = "discount_percent", nullable = false)
  @Schema(description = "Процент скидки", example = "10")
  private Integer discountPercent = 0;

  @Column(name = "additional_info", columnDefinition = "TEXT")
  @Schema(description = "Дополнительная информация")
  private String additionalInfo;

  @Column(name = "is_client", nullable = false)
  @Schema(description = "Признак, что контакт является клиентом", example = "true")
  private Boolean isClient = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  @JsonIgnore
  @Schema(description = "Пользователь, связанный с контактом")
  private User user;

  @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonIgnore
  @Schema(description = "Заявки, связанные с контактом")
  private List<Application> applications;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  @Schema(description = "Дата и время создания", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  @Schema(description = "Дата и время последнего обновления", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime updatedAt;

  @ManyToMany
  @JsonIgnore
  private List<Tour> tours;

  @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonIgnore
  private List<ClientTour> clientTours;
}