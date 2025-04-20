package com.example.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UserInfoDto;
import com.example.demo.models.User;
import com.example.demo.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/users")
@Tag(name = "Пользователи", description = "API для работы с пользователями")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

  private final UserRepository userRepository;

  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получение информации о пользователе по ID", description = "Возвращает информацию о пользователе по указанному ID", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<User> getUserById(
      @Parameter(description = "ID пользователя", required = true) @PathVariable Long id) {

    User user = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + id + " не найден"));

    UserInfoDto userInfo = new UserInfoDto();
    userInfo.setId(user.getId());
    userInfo.setEmail(user.getEmail());
    userInfo.setFullName(user.getFullName());
    userInfo.setRole(user.getRole());

    return ResponseEntity.ok(user);
  }
}