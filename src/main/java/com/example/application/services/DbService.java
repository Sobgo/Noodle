package com.example.application.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.application.data.entity.Course;
import com.example.application.data.entity.CourseInfo;
import com.example.application.data.entity.Role;
import com.example.application.data.entity.User;
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

	// course

	public List<CourseInfo> getAllInfoOnly() {
		return courseRepository.findBy();	
	}

	public Course getCourse(Long courseId) {
		return courseRepository.findById(courseId).get();
	}

	public Course saveCourse(Course course) {
		return courseRepository.save(course);
	}

	public void deleteCourse(Course course) {
		courseRepository.delete(course);
	}

	// user

	public List<User> getUsers() {
		return userRepository.findAll();
	}

	public User getUser(Long userId) {
		return userRepository.findById(userId).get();
	}

	public User saveUser(User user) {
		return userRepository.save(user);
	}

	// role

	public Role getRole(Long roleId) {
		return roleRepository.findById(roleId).get();
	}

	public Role saveRole(Role role) {
		return roleRepository.save(role);
	}

	public void grantRole(User user, Role role) {
		user.getRoles().add(role);
		userRepository.save(user);
	}
}
