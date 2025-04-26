package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.ContactDto;
import com.example.demo.dto.UserInfoDto;
import com.example.demo.models.AgeGroup;
import com.example.demo.models.Contact;
import com.example.demo.models.Gender;
import com.example.demo.models.User;
import com.example.demo.models.UserRole;
import com.example.demo.repository.ContactRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.PasswordGenerator;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContactService {

  private final ContactRepository contactRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;

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
        .isClient(true) // По умолчанию не клиент, пока не подаст заявку
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

  public List<ContactDto> getAllContacts() {
    return contactRepository.findAll().stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  public UserInfoDto createUserFromContact(ContactDto contactDto) {
    // Получаем существующий контакт
    Contact contact = contactRepository.findById(contactDto.getId())
        .orElseThrow(() -> new EntityNotFoundException("Контакт с ID " + contactDto.getId() + " не найден"));

    // Генерируем пароль
    String rawPassword = PasswordGenerator.generateSimplePassword();

    // Создаем пользователя
    User user = new User();
    user.setFullName(contactDto.getFullName());
    user.setEmail(contactDto.getEmail());
    user.setPassword(passwordEncoder.encode(rawPassword));
    user.setRole(UserRole.TOURIST);

    // Устанавливаем связь в обоих направлениях
    user.setContact(contact);

    // Сохраняем пользователя
    user = userRepository.save(user);

    // Устанавливаем обратную связь и сохраняем контакт
    contact.setUser(user);
    contact.setIsClient(true);
    contactRepository.save(contact);

    // Отправляем email с данными для входа
    emailService.sendEmail(user.getEmail(), "Добро пожаловать в нашу систему!",
        "Здравствуйте, " + user.getFullName() + "!\n\n" +
            "Мы рады приветствовать Вас в нашей туристической системе. " +
            "Для Вас был создан личный кабинет со следующими данными:\n\n" +
            "Email: " + user.getEmail() + "\n" +
            "Пароль: " + rawPassword + "\n\n" +
            "Рекомендуем сменить пароль при первом входе в систему.\n\n" +
            "С уважением,\n" +
            "Команда поддержки");

    return convertUserToDto(user);
  }

  private UserInfoDto convertUserToDto(User user) {
    return UserInfoDto.builder()
        .id(user.getId())
        .fullName(user.getFullName())
        .email(user.getEmail())
        .role(user.getRole())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .contact(user.getContact())
        .build();
  }

  public ContactDto createContact(ContactDto contactDto) {
    Contact contact = contactRepository.save(convertToEntity(contactDto));
    return convertToDto(contact);
  }

  public ContactDto updateContact(Long id, ContactDto contactDto) {
    Contact contact = contactRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Контакт с ID " + id + " не найден"));
    contact = contactRepository.save(convertToEntity(contactDto));
    return convertToDto(contact);
  }

  private Contact convertToEntity(ContactDto contactDto) {
    return Contact.builder()
        .id(contactDto.getId())
        .fullName(contactDto.getFullName())
        .email(contactDto.getEmail())
        .phoneNumber(contactDto.getPhoneNumber())
        .ageGroup(contactDto.getAgeGroup())
        .gender(contactDto.getGender())
        .isClient(false)
        .discountPercent(contactDto.getDiscountPercent())
        .preferredTourType(contactDto.getPreferredTourType())
        .additionalInfo(contactDto.getAdditionalInfo())
        .build();
  }

  private ContactDto convertToDto(Contact contact) {
    return ContactDto.builder()
        .id(contact.getId())
        .fullName(contact.getFullName())
        .email(contact.getEmail())
        .phoneNumber(contact.getPhoneNumber())
        .ageGroup(contact.getAgeGroup())
        .gender(contact.getGender())
        .isClient(contact.getIsClient())
        .discountPercent(contact.getDiscountPercent())
        .preferredTourType(contact.getPreferredTourType())
        .additionalInfo(contact.getAdditionalInfo())
        .discountPercent(contact.getDiscountPercent())
        .build();
  }

  public ContactDto updateDiscountPercent(Long id, Integer discountPercent) {
    Contact contact = contactRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Контакт с ID " + id + " не найден"));
    contact.setDiscountPercent(discountPercent);
    contactRepository.save(contact);
    return convertToDto(contact);
  }
}