package com.example.demo.utils;

import java.security.SecureRandom;

/**
 * Утилитарный класс для генерации случайных паролей
 */
public class PasswordGenerator {

  private static final String LOWER = "abcdefghjkmnpqrstuvwxyz";
  private static final String UPPER = "ABCDEFGHJKMNPQRSTUVWXYZ";
  private static final String DIGITS = "23456789";
  private static final String SPECIAL = "!@#$%^&*";
  private static final String ALL = LOWER + UPPER + DIGITS + SPECIAL;

  private static final SecureRandom random = new SecureRandom();

  /**
   * Генерирует случайный пароль заданной длины
   * 
   * @param length длина пароля
   * @return случайный пароль
   */
  public static String generateRandomPassword(int length) {
    if (length < 6) {
      length = 6; // Минимальная длина для надежного пароля
    }

    StringBuilder password = new StringBuilder(length);

    // Добавляем по одному символу из каждой категории для обеспечения надежности
    password.append(LOWER.charAt(random.nextInt(LOWER.length())));
    password.append(UPPER.charAt(random.nextInt(UPPER.length())));
    password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
    password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

    // Заполняем остаток пароля случайными символами
    for (int i = 4; i < length; i++) {
      password.append(ALL.charAt(random.nextInt(ALL.length())));
    }

    // Перемешиваем пароль для большей случайности
    char[] passwordArray = password.toString().toCharArray();
    for (int i = 0; i < passwordArray.length; i++) {
      int randomIndex = random.nextInt(passwordArray.length);
      char temp = passwordArray[i];
      passwordArray[i] = passwordArray[randomIndex];
      passwordArray[randomIndex] = temp;
    }

    return new String(passwordArray);
  }

  /**
   * Генерирует простой запоминающийся пароль длиной 8 символов
   * 
   * @return простой запоминающийся пароль
   */
  public static String generateSimplePassword() {
    return generateRandomPassword(8);
  }
}