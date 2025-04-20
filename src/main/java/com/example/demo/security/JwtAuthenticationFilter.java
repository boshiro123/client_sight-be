package com.example.demo.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;

  public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      // Получаем JWT токен из заголовка Authorization
      String token = getTokenFromRequest(request);

      // Проверяем токен
      if (token != null && jwtTokenProvider.validateToken(token)) {
        // Получаем email пользователя из токена
        String email = jwtTokenProvider.getUserEmailFromToken(token);

        // Загружаем данные пользователя
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Создаем объект аутентификации
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Устанавливаем аутентификацию в SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception e) {
      logger.error("Не удалось установить аутентификацию пользователя: " + e.getMessage());
    }

    filterChain.doFilter(request, response);
  }

  private String getTokenFromRequest(HttpServletRequest request) {
    // Получаем заголовок Authorization
    String bearerToken = request.getHeader("Authorization");

    // Проверяем, что заголовок не пустой и начинается с "Bearer "
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      // Возвращаем токен без префикса "Bearer "
      return bearerToken.substring(7);
    }

    return null;
  }
}