package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Tour;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
  // Здесь можно добавить дополнительные методы для поиска туров
  // например, поиск по стране, сезону и т.д.
}