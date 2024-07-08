package com.example.application.services;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.application.data.entity.Role;
import com.example.application.data.entity.User;
import com.example.application.data.entity.Course.Course;
import com.example.application.data.entity.Course.CourseInfo;
import com.example.application.data.entity.Course.Panel;
import com.example.application.data.repository.CourseRepository;
import com.example.application.data.repository.PanelRepository;
import com.example.application.data.repository.RoleRepository;
import com.example.application.data.repository.UserRepository;

@Service
public class DbService {
	private final CourseRepository courseRepository;
	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final PanelRepository panelRepository;

	public DbService(
		CourseRepository courseRepository, 
		RoleRepository roleRepository,
		UserRepository userRepository,
		PanelRepository panelRepository
	) {
		this.courseRepository = courseRepository;
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
		this.panelRepository = panelRepository;
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

	public void deleteCourse(Long courseId) {
		Course course = getCourse(courseId);
		
		course.getPanels().forEach(panel -> panelRepository.delete(panel));

		// remove roles from users
		Long roleId = course.getCourseRole().getId();
		Set<User> users = roleRepository.findById(roleId).get().getUsers();

		users.forEach(user -> {
			user.getRoles().remove(course.getCourseRole());
			userRepository.save(user);
		});

		courseRepository.delete(course);
	}

	public List<CourseInfo> getUserRegisteredCourses(Long userId) {
		User user = userRepository.findById(userId).get();

		return getAllInfoOnly().stream()
			.filter(course -> {
				return user.getRoles().stream().anyMatch(role -> course.getCourseRole().equals(role));
		}).toList();
	}

	public List<Panel> getCoursePanels(Long courseId) {
		return getCourse(courseId).getPanels();
	}

	public Course addPanel(Long courseId, Panel panel) {
		Course course = getCourse(courseId);
		panel.setCourse(course);

		Panel savedPanel = panelRepository.save(panel);
		course.getPanels().add(savedPanel);
		Course savedCourse = courseRepository.save(course);

		return savedCourse;
	}

	public Course deletePanel(Long CourseId, Long panelId) {
		Course course = getCourse(CourseId);
		Panel panel = panelRepository.findById(panelId).get();
		course.getPanels().remove(panel);
		Course savedCourse = courseRepository.save(course);
		panelRepository.delete(panel);

		return savedCourse;
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

	public void deleteUser(Long userId) {
		User user = getUser(userId);

		// if user is admin, return
		if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"))) {
			return;
		}

		// remove all roles from user
		user.getRoles().clear();
		userRepository.save(user);

		// for each user course, set owner to null
		user.getCourses().forEach(course -> {
			course.setOwner(null);
			courseRepository.save(course);
		});

		userRepository.delete(user);
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

	public void revokeRole(Long userId, Long roleId) {
		User user = userRepository.findById(userId).get();
		Role role = roleRepository.findById(roleId).get();

		user.getRoles().remove(role);
		userRepository.save(user);
	}
}
