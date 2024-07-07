package com.example.application.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.application.data.entity.CourseClasses.Course;
import com.example.application.data.entity.CourseClasses.CourseInfo;

import jakarta.transaction.Transactional;

@Transactional
public interface CourseRepository extends JpaRepository<Course, Long> {
	List<CourseInfo> findBy();

	CourseInfo findInfoById(Long id);
}
