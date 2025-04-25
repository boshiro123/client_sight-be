package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;

  public void sendEmail(String to, String subject, String text) {
    log.info("Отправка письма на адрес: {}", to);
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(to);
      message.setSubject(subject);
      message.setText(text);
      mailSender.send(message);
      log.info("Письмо успешно отправлено на адрес: {}", to);
    } catch (Exception e) {
      log.error("Ошибка при отправке письма на адрес {}: {}", to, e.getMessage());
      throw e;
    }
  }
}