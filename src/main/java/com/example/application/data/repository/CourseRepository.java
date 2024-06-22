package com.example.application.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.application.data.entity.Course;
import com.example.application.data.entity.CourseInfo;

public interface CourseRepository extends JpaRepository<Course, Long> {
	List<CourseInfo> findBy();
}
