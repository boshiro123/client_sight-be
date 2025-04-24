package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Application;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

  /**
   * Находит все заявки с загрузкой связанных сущностей
   */
  @Query("SELECT a FROM Application a LEFT JOIN FETCH a.tour LEFT JOIN FETCH a.clientTour")
  List<Application> findAllWithDetails();

  /**
   * Находит заявку по ID с загрузкой связанных сущностей
   */
  @Query("SELECT a FROM Application a LEFT JOIN FETCH a.tour LEFT JOIN FETCH a.clientTour WHERE a.id = :id")
  Optional<Application> findByIdWithDetails(@Param("id") Long id);

  // Дополнительные методы при необходимости

  @Query("SELECT a FROM Application a LEFT JOIN FETCH a.tour LEFT JOIN FETCH a.clientTour WHERE a.email = :email")
  List<Application> findAllByEmail(@Param("email") String email);
}