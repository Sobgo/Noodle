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

	public CourseInfo getCourseInfo(Long courseId) {
		return courseRepository.findInfoById(courseId);
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

	public List<CourseInfo> getUserRegisteredCourses(Long userId) {
		User user = userRepository.findById(userId).get();

		return getAllInfoOnly().stream()
			.filter(course -> {
				return user.getRoles().stream().anyMatch(role -> course.getCourseRole().equals(role));
		}).toList();
	}

	// user

	public List<User> getUsers() {
		return userRepository.findAll();
	}

	public User getUser(Long userId) {
		return userRepository.findById(userId).get();
	}

	public User getUserByUsername(String username) {
		return userRepository.findByUsername(username);
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

	public void grantRole(Long userId, Long roleId) {
		User user = userRepository.findById(userId).get();
		Role role = roleRepository.findById(roleId).get();

		user.getRoles().add(role);
		userRepository.save(user);
	}
}
