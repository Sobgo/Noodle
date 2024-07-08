package com.example.application.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.application.data.entity.Course.Panel;

public interface PanelRepository extends JpaRepository<Panel, Long> {}
