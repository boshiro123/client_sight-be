package com.example.demo.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "client_tours", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "contact_id", "tour_id" })
})
@Schema(description = "Модель связи клиента с туром")
public class ClientTour {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "Уникальный идентификатор связи клиент-тур", example = "1")
  private Long id;

  @NotNull(message = "ID контакта обязателен")
  @Column(name = "contact_id", nullable = false)
  @Schema(description = "ID контакта клиента", example = "1", required = true)
  private Long contactId;

  @NotNull(message = "ID тура обязателен")
  @Column(name = "tour_id", nullable = false)
  @Schema(description = "ID тура", example = "1", required = true)
  private Long tourId;

  @Column(name = "application_id")
  @Schema(description = "ID заявки, если клиент был добавлен через заявку", example = "1")
  private Long applicationId;

  @Column(name = "is_active", nullable = false)
  @Schema(description = "Признак активности записи", example = "true")
  private Boolean isActive = true;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "contact_id", insertable = false, updatable = false)
  @JsonIgnore
  private Contact contact;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tour_id", insertable = false, updatable = false)
  @JsonIgnore
  private Tour tour;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "application_id", insertable = false, updatable = false)
  @JsonIgnore
  private Application application;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  @Schema(description = "Дата и время создания", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  @Schema(description = "Дата и время последнего обновления", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime updatedAt;
}