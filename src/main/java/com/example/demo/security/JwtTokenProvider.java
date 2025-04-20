package com.example.demo.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

  @Value("${jwt.secret:defaultSecretKeyForDevelopmentPurposesOnly}")
  private String jwtSecret;

  @Value("${jwt.expiration:86400000}") // 24 часа по умолчанию
  private long jwtExpirationInMs;

  private Key getSigningKey() {
    // Создаем секретный ключ, подходящий для HS512
    return Keys.secretKeyFor(SignatureAlgorithm.HS512);
  }

  // Генерация JWT токена
  public String generateToken(Authentication authentication) {
    String email = authentication.getName();
    Date currentDate = new Date();
    Date expiryDate = new Date(currentDate.getTime() + jwtExpirationInMs);

    // Используем безопасный ключ для HS512
    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(expiryDate)
        .signWith(getSigningKey())
        .compact();
  }

  // Получение email из токена
  public String getUserEmailFromToken(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

    return claims.getSubject();
  }

  // Валидация токена
  public boolean validateToken(String token) {
    try {
      // Парсим токен
      Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token);

      return true;
    } catch (Exception e) {
      return false;
    }
  }
}