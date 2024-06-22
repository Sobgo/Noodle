package com.example.application.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.application.data.entity.Course;
import com.example.application.data.entity.CourseInfo;
import com.example.application.data.repository.CourseRepository;
import com.example.application.data.repository.RoleRepository;
import com.example.application.data.repository.UserRepository;

@Service
public class DbService {
	private final CourseRepository courseRepository;
	private final RoleRepository roleRepository;
	private final UserRepository userRepository;

	public DbService(
		CourseRepository courseRepository, 
		RoleRepository roleRepository,
		UserRepository userRepository
	) {
		this.courseRepository = courseRepository;
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
	}

	public List<CourseInfo> getAllInfoOnly() {
		return courseRepository.findBy();	
	}

	public Course getCurse(Long courseId) {
		return courseRepository.findById(courseId).get();
	}
}
