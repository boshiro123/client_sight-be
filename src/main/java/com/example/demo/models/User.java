package com.example.demo.models;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Schema(description = "Модель пользователя системы")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "Уникальный идентификатор пользователя", example = "1")
  private Long id;

  @NotBlank(message = "ФИО обязательно")
  @Size(min = 3, max = 100, message = "ФИО должно быть от 3 до 100 символов")
  @Column(name = "full_name", nullable = false)
  @Schema(description = "ФИО пользователя", example = "Иванов Иван Иванович", required = true)
  private String fullName;

  @NotBlank(message = "Email обязателен")
  @Email(message = "Email должен быть корректным")
  @Column(unique = true, nullable = false)
  @Schema(description = "Email пользователя", example = "user@example.com", required = true)
  private String email;

  @NotBlank(message = "Пароль обязателен")
  @JsonProperty(access = Access.WRITE_ONLY)
  @Column(nullable = false)
  @Schema(description = "Пароль пользователя", example = "password123", required = true, accessMode = Schema.AccessMode.WRITE_ONLY)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Schema(description = "Роль пользователя", example = "TOURIST", required = true)
  private UserRole role = UserRole.TOURIST;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  @Schema(description = "Дата и время создания", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  @Schema(description = "Дата и время последнего обновления", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime updatedAt;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonIgnore
  private Contact contact;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonIgnore
  private List<Application> applications;

  @Override
  @JsonIgnore
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  @Override
  @JsonIgnore
  public String getUsername() {
    return email;
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isEnabled() {
    return true;
  }
}