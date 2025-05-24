package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.models.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
  Optional<Contact> findByEmail(String email);

  boolean existsByEmail(String email);

  @Query("SELECT c FROM Contact c WHERE c.user.id = :userId")
  Optional<Contact> findByUserId(Long userId);

  @Transactional
  void deleteByUserId(Long userId);

  @Query("SELECT c FROM Contact c WHERE c.user IS NULL")
  java.util.List<Contact> findOrphanedContacts();
}