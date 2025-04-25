package com.example.demo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.dto.UserInfoDto;
import com.example.demo.models.Contact;
import com.example.demo.models.User;
import com.example.demo.models.UserRole;
import com.example.demo.repository.ContactRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ContactService;
import com.example.demo.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@Tag(name = "Пользователи", description = "API для работы с пользователями")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

  private final ContactService contactService;

  private final UserRepository userRepository;

  private final UserService userService;

  @GetMapping("/{id}")
  @Operation(summary = "Получение информации о пользователе по ID", description = "Возвращает информацию о пользователе по указанному ID", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<UserInfoDto> getUserById(
      @Parameter(description = "ID пользователя", required = true) @PathVariable Long id) {

    User user = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + id + " не найден"));

    Contact contact = null;
    if (user.getRole().equals(UserRole.TOURIST)) {
      contact = contactService.getContactByUserId(id);
    }

    UserInfoDto userInfo = new UserInfoDto();
    userInfo.setId(user.getId());
    userInfo.setEmail(user.getEmail());
    userInfo.setFullName(user.getFullName());
    userInfo.setRole(user.getRole());
    userInfo.setContact(contact);
    userInfo.setCreatedAt(user.getCreatedAt());
    userInfo.setUpdatedAt(user.getUpdatedAt());

    return ResponseEntity.ok(userInfo);
  }

  @GetMapping("/all-tourists")
  @Operation(summary = "Получение всех туристов", description = "Возвращает всех туристов", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<List<UserInfoDto>> getAllTourists() {
    List<UserInfoDto> tourists = userService.getAllTourists();
    return ResponseEntity.ok(tourists);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Обновление информации о пользователе", description = "Обновляет информацию о пользователе по указанному ID", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<UserInfoDto> updateUser(
      @Parameter(description = "ID пользователя", required = true) @PathVariable Long id,
      @Parameter(description = "Информация для обновления", required = true) @RequestBody UserInfoDto userInfo) {

    // Проверяем, что ID в URL и в теле запроса совпадают
    if (!id.equals(userInfo.getId())) {
      throw new IllegalArgumentException("ID в URL и в теле запроса не совпадают");
    }

    // Сначала обновляем пользователя
    UserInfoDto updatedUserInfo = userService.updateUser(id, userInfo);

    // Если передана информация о контакте, обновляем и его
    if (userInfo.getContact() != null) {
      try {
        // Получаем текущий контакт пользователя
        Contact existingContact = contactService.getContactByUserId(id);

        // Обновляем поля контакта из запроса
        if (userInfo.getContact().getPhoneNumber() != null) {
          existingContact.setPhoneNumber(userInfo.getContact().getPhoneNumber());
        }
        if (userInfo.getContact().getAgeGroup() != null) {
          existingContact.setAgeGroup(userInfo.getContact().getAgeGroup());
        }
        if (userInfo.getContact().getGender() != null) {
          existingContact.setGender(userInfo.getContact().getGender());
        }
        if (userInfo.getContact().getPreferredTourType() != null) {
          existingContact.setPreferredTourType(userInfo.getContact().getPreferredTourType());
        }
        if (userInfo.getContact().getAdditionalInfo() != null) {
          existingContact.setAdditionalInfo(userInfo.getContact().getAdditionalInfo());
        }
        existingContact.setFullName(updatedUserInfo.getFullName());
        existingContact.setEmail(updatedUserInfo.getEmail());

        // Сохраняем обновленный контакт
        Contact updatedContact = contactService.updateContact(existingContact);
        updatedUserInfo.setContact(updatedContact);
      } catch (EntityNotFoundException e) {
        // Если контакта нет, предупреждаем в логах, но не падаем
        // Это может произойти, если пользователь создан, но контакт ещё нет
        System.out.println("Контакт для пользователя с ID " + id + " не найден");
      }
    }

    return ResponseEntity.ok(updatedUserInfo);
  }
}
