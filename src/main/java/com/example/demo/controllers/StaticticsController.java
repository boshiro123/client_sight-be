package com.example.demo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.StatisticDto;
import com.example.demo.service.StatisticService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/statistics")
@Tag(name = "Статистика", description = "API для работы со статистикой")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class StaticticsController {

  private final StatisticService statisticService;

  @GetMapping("/employee")
  @Operation(summary = "Получение статистики", description = "Получение статистики для сотрудника")
  public ResponseEntity<StatisticDto> getStatistics() {
    return ResponseEntity.ok(statisticService.getStatistics());
  }

}