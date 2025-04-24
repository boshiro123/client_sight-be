package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.AgeGroup;
import com.example.demo.models.Contact;
import com.example.demo.models.Gender;
import com.example.demo.models.User;
import com.example.demo.repository.ContactRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContactService {

  private final ContactRepository contactRepository;

  /**
   * Создает контакт из данных пользователя
   * Используется при регистрации нового пользователя
   */
  @Transactional
  public Contact createContactFromUser(User user) {
    // Проверяем, существует ли уже контакт с таким email
    if (contactRepository.existsByEmail(user.getEmail())) {
      return contactRepository.findByEmail(user.getEmail()).orElse(null);
    }

    // Создаем новый контакт с данными из пользователя
    Contact contact = Contact.builder()
        .fullName(user.getFullName())
        .email(user.getEmail())
        .phoneNumber("Не указан") // Значение по умолчанию
        .ageGroup(AgeGroup.UNDER_18) // Значение по умолчанию
        .gender(Gender.OTHER) // Значение по умолчанию
        .user(user)
        .isClient(false) // По умолчанию не клиент, пока не подаст заявку
        .discountPercent(0)
        .build();

    return contactRepository.save(contact);
  }

  public Contact getContactByUserId(Long id) {
    return contactRepository.findByUserId(id)
        .orElseThrow(() -> new EntityNotFoundException("Контакт с ID " + id + " не найден"));
  }

  public Contact updateContact(Contact contact) {
    return contactRepository.save(contact);
  }

}