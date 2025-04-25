package com.example.demo.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApplicationDto;
import com.example.demo.dto.ContactDto;
import com.example.demo.dto.StatusUpdateDto;
import com.example.demo.dto.UserInfoDto;
import com.example.demo.models.ApplicationStatus;
import com.example.demo.service.ApplicationService;
import com.example.demo.service.ContactService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/contacts")
@Tag(name = "Контакты", description = "API для работы с контактами")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ContactController {

  private final ContactService contactService;

  @GetMapping("/all")
  @Operation(summary = "Получение всех контактов", description = "Возвращает всех контактов", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<List<ContactDto>> getAllContacts() {
    List<ContactDto> contacts = contactService.getAllContacts();
    return ResponseEntity.ok(contacts);
  }

  @PostMapping("/create")
  @Operation(summary = "Создание контакта", description = "Создает новый контакт", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<ContactDto> createContact(@RequestBody ContactDto contactDto) {
    ContactDto createdContact = contactService.createContact(contactDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdContact);
  }

  @PutMapping("/update/{id}")
  @Operation(summary = "Обновление контакта", description = "Обновляет существующий контакт", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<ContactDto> updateContact(@PathVariable Long id, @RequestBody ContactDto contactDto) {
    ContactDto updatedContact = contactService.updateContact(id, contactDto);
    return ResponseEntity.ok(updatedContact);
  }

  @PostMapping("/create-user-from-contact")
  @Operation(summary = "Создание пользователя из контакта", description = "Создает пользователя из контакта", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<UserInfoDto> createUserFromContact(@RequestBody ContactDto contactDto) {
    UserInfoDto createdUser = contactService.createUserFromContact(contactDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }
}
