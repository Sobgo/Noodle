package com.example.application.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.application.data.entity.Course;
import com.example.application.data.repository.CourseRepository;
import com.example.application.data.repository.RoleRepository;

@Service
public class DbService {
	private final CourseRepository courseRepository;
	private final RoleRepository roleRepository;

	public DbService(
		CourseRepository courseRepository, 
		RoleRepository roleRepository
	) {
		this.courseRepository = courseRepository;
		this.roleRepository = roleRepository;
	}
	
	public List<Course> gatAllCourses() {
		return courseRepository.findAll();
	}
}
