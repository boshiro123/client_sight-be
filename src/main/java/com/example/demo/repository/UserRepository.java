package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.User;
import com.example.demo.models.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByFullName(String fullName);

  Optional<User> findByEmail(String email);

  boolean existsByFullName(String fullName);

  boolean existsByEmail(String email);

  List<User> findAllByRole(UserRole role);

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM users WHERE id = :userId", nativeQuery = true)
  void deleteUserWithoutCascade(@Param("userId") Long userId);
}