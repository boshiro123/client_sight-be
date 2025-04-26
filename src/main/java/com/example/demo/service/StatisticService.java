package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.dto.StatisticDto;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.TourRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticService {

  private final TourRepository tourRepository;
  private final ApplicationRepository applicationRepository;
  private final UserRepository clientTourRepository;

  public StatisticDto getStatistics() {
    return StatisticDto.builder()
        .tourCount(tourRepository.count())
        .applicationCount(applicationRepository.count())
        .clientCount(clientTourRepository.count())
        .build();
  }
}
