package com.example.application.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.application.data.entity.User;

import jakarta.transaction.Transactional;

@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
}
