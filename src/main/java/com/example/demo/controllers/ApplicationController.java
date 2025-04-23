package com.example.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/applications")
@Tag(name = "Пользователи", description = "API для работы с пользователями")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ApplicationController {

  // private final UserRepository userRepository;

  // @PostMapping()
  // @Operation(summary = "Сохранение заявки на тур", description = "Возвращает
  // информацию о заявке")
  // public ResponseEntity<Application> saveApplication() {

  // User user = userRepository.findById(id)
  // .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + id + "
  // не найден"));

  // UserInfoDto userInfo = new UserInfoDto();
  // userInfo.setId(user.getId());
  // userInfo.setEmail(user.getEmail());
  // userInfo.setFullName(user.getFullName());
  // userInfo.setRole(user.getRole());

  // return ResponseEntity.ok(user);
  // }
}