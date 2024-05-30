package com.example.application.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.application.data.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {}
