package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.UserInfoDto;
import com.example.demo.models.AgeGroup;
import com.example.demo.models.Contact;
import com.example.demo.models.Gender;
import com.example.demo.models.User;
import com.example.demo.repository.ContactRepository;
import com.example.demo.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

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

  private UserInfoDto convertToDto(User user) {
    return UserInfoDto.builder()
        .id(user.getId())
        .fullName(user.getFullName())
        .email(user.getEmail())
        .role(user.getRole())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
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