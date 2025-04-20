package com.example.demo.security;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException {

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=UTF-8");

    ErrorResponse errorResponse = new ErrorResponse(
        HttpServletResponse.SC_UNAUTHORIZED,
        "Ошибка аутентификации: " + authException.getMessage(),
        System.currentTimeMillis());

    PrintWriter writer = response.getWriter();
    writer.write(objectMapper.writeValueAsString(errorResponse));
    writer.flush();
  }

  static class ErrorResponse {
    private int status;
    private String message;
    private long timestamp;

    public ErrorResponse(int status, String message, long timestamp) {
      this.status = status;
      this.message = message;
      this.timestamp = timestamp;
    }

    public int getStatus() {
      return status;
    }

    public void setStatus(int status) {
      this.status = status;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public long getTimestamp() {
      return timestamp;
    }

    public void setTimestamp(long timestamp) {
      this.timestamp = timestamp;
    }
  }
}