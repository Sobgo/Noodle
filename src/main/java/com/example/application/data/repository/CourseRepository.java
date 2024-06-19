package com.example.application.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.application.data.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {}
