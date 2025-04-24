package com.example.demo.models;

import java.time.LocalDateTime;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
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
@Table(name = "applications")
@Schema(description = "Модель заявки на тур")
public class Application {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "Уникальный идентификатор заявки", example = "1")
  private Long id;

  @NotBlank(message = "ФИО обязательно")
  @Column(name = "full_name", nullable = false)
  @Schema(description = "ФИО заявителя", example = "Иванов Иван Иванович", required = true)
  private String fullName;

  @NotBlank(message = "Номер телефона обязателен")
  @Column(name = "phone_number", nullable = false)
  @Schema(description = "Номер телефона", example = "+79001234567", required = true)
  private String phoneNumber;

  @NotBlank(message = "Email обязателен")
  @Email(message = "Email должен быть корректным")
  @Column(nullable = false)
  @Schema(description = "Email", example = "ivan@example.com", required = true)
  private String email;

  @NotNull(message = "ID тура обязателен")
  @Column(name = "tour_id", nullable = false)
  @Schema(description = "ID тура", example = "1", required = true)
  private Long tourId;

  @Column(name = "user_id")
  @Schema(description = "ID пользователя, если заявка от зарегистрированного пользователя", example = "1")
  private Long userId;

  @NotNull(message = "Статус заявки обязателен")
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Schema(description = "Статус заявки", example = "PENDING", required = true)
  private ApplicationStatus status = ApplicationStatus.PENDING;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tour_id", insertable = false, updatable = false)
  @JsonIgnore
  private Tour tour;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", insertable = false, updatable = false)
  @JsonIgnore
  private User user;

  @Column(name = "contact_id")
  @Schema(description = "ID контакта", example = "1")
  private Long contactId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "contact_id", insertable = false, updatable = false)
  @JsonIgnore
  private Contact contact;

  @OneToOne(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonIgnore
  private ClientTour clientTour;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  @Schema(description = "Дата и время создания", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  @Schema(description = "Дата и время последнего обновления", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime updatedAt;
}