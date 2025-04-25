package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.User;
import com.example.demo.models.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByFullName(String fullName);

  Optional<User> findByEmail(String email);

  boolean existsByFullName(String fullName);

  boolean existsByEmail(String email);

  List<User> findAllByRole(UserRole role);
}