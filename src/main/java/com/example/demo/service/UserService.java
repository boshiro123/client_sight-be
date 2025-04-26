package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;
  private final ContactRepository contactRepository;

  @Transactional
  public UserInfoDto updateUser(Long id, UserInfoDto userInfo) {
    // Получаем существующего пользователя из базы данных
    User existingUser = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + id + " не найден"));

    // Обновляем только поля, которые можно изменить
    existingUser.setFullName(userInfo.getFullName());
    existingUser.setEmail(userInfo.getEmail());
    if (userInfo.getRole() != null) {
      existingUser.setRole(userInfo.getRole());
    }

    // Пароль не трогаем, он остается прежним
    // Сохраняем обновленного пользователя
    User updatedUser = userRepository.save(existingUser);
    return convertToDto(updatedUser);
  }

  @Transactional(readOnly = true)
  public List<UserInfoDto> getAllTourists() {
    List<User> tourists = userRepository.findAllByRole(UserRole.TOURIST);
    return tourists.stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<UserInfoDto> getAllEmployees() {
    List<User> employees = userRepository.findAllByRole(UserRole.EMPLOYEE);
    return employees.stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  @Transactional
  public UserInfoDto createEmployee(UserInfoDto userInfo) {
    // Генерируем пароль
    String rawPassword = PasswordGenerator.generateSimplePassword();

    // Создаем пользователя
    User user = new User();
    user.setFullName(userInfo.getFullName());
    user.setEmail(userInfo.getEmail());
    user.setPassword(passwordEncoder.encode(rawPassword));
    user.setRole(UserRole.EMPLOYEE);

    // Сохраняем пользователя
    user = userRepository.save(user);

    // Отправляем email с данными для входа
    emailService.sendEmail(user.getEmail(), "Добро пожаловать в нашу систему!",
        "Здравствуйте, " + user.getFullName() + "!\n\n" +
            "Мы рады приветствовать Вас в нашей туристической системе. " +
            "Для Вас был создан личный кабинет сотрудника со следующими данными:\n\n" +
            "Email: " + user.getEmail() + "\n" +
            "Пароль: " + rawPassword + "\n\n" +
            "Рекомендуем сменить пароль при первом входе в систему.\n\n" +
            "С уважением,\n" +
            "Команда поддержки");

    return convertToDto(user);
  }

  @Transactional
  public void deleteUser(Long id) {
    userRepository.deleteById(id);
    contactRepository.deleteByUserId(id);
  }

  private UserInfoDto convertToDto(User user) {
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

  private User convertToEntity(UserInfoDto userInfo) {
    return User.builder()
        .id(userInfo.getId())
        .fullName(userInfo.getFullName())
        .email(userInfo.getEmail())
        .build();
  }
}