package com.example.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.JwtAuthResponse;
import com.example.demo.dto.LoginDto;
import com.example.demo.dto.RegisterDto;
import com.example.demo.models.User;
import com.example.demo.models.UserRole;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtTokenProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Аутентификация", description = "API для аутентификации и регистрации пользователей")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
      PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @PostMapping("/register")
  @Operation(summary = "Регистрация нового пользователя", description = "Регистрирует нового пользователя в системе и возвращает JWT токен")
  public ResponseEntity<JwtAuthResponse> registerUser(
      @Parameter(description = "Данные для регистрации", required = true) @Valid @RequestBody RegisterDto registerDto) {

    // Проверка на существование email
    if (userRepository.existsByEmail(registerDto.getEmail())) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // Создание нового пользователя
    User user = new User();
    user.setFullName(registerDto.getFullName());
    user.setEmail(registerDto.getEmail());
    user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
    user.setRole(UserRole.TOURIST); // По умолчанию - турист

    userRepository.save(user);

    // Аутентификация нового пользователя и генерация токена
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(registerDto.getEmail(), registerDto.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String token = jwtTokenProvider.generateToken(authentication);

    return ResponseEntity.status(HttpStatus.CREATED).body(new JwtAuthResponse(token, user.getId()));
  }

  @PostMapping("/login")
  @Operation(summary = "Аутентификация пользователя", description = "Аутентифицирует пользователя и возвращает JWT токен")
  public ResponseEntity<JwtAuthResponse> authenticateUser(
      @Parameter(description = "Данные для входа", required = true) @Valid @RequestBody LoginDto loginDto) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String token = jwtTokenProvider.generateToken(authentication);

    // Получение id пользователя
    User user = userRepository.findByEmail(loginDto.getEmail())
        .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

    return ResponseEntity.ok(new JwtAuthResponse(token, user.getId()));
  }
}