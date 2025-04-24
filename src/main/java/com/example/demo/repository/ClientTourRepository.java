package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.models.ClientTour;

@Repository
public interface ClientTourRepository extends JpaRepository<ClientTour, Long> {

  /**
   * Находит все активные связи клиент-тур
   */
  List<ClientTour> findByIsActiveTrue();

  /**
   * Находит все связи для конкретного контакта (клиента)
   */
  List<ClientTour> findByContactId(Long contactId);

  /**
   * Находит все связи для конкретного тура
   */
  List<ClientTour> findByTourId(Long tourId);

  /**
   * Находит связь по ID заявки
   */
  Optional<ClientTour> findByApplicationId(Long applicationId);

  /**
   * Находит связь по ID контакта и ID тура
   */
  Optional<ClientTour> findByContactIdAndTourId(Long contactId, Long tourId);

  /**
   * Находит все связи клиент-тур с загрузкой связанных сущностей
   */
  @Query("SELECT ct FROM ClientTour ct LEFT JOIN FETCH ct.contact LEFT JOIN FETCH ct.tour LEFT JOIN FETCH ct.application WHERE ct.isActive = true")
  List<ClientTour> findAllActiveWithDetails();

  /**
   * Находит связь по ID с загрузкой связанных сущностей
   */
  @Query("SELECT ct FROM ClientTour ct LEFT JOIN FETCH ct.contact LEFT JOIN FETCH ct.tour LEFT JOIN FETCH ct.application WHERE ct.id = :id")
  Optional<ClientTour> findByIdWithDetails(@Param("id") Long id);
}