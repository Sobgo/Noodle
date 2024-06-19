package com.example.application.views.admin;

import java.util.List;

import com.example.application.data.entity.Course;
import com.example.application.services.DbService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "admin", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AdminView extends VerticalLayout {
	
	private DbService db;
	
	public AdminView(DbService db) {
		this.db = db;
		constructUI();
	}

	private void constructUI() {
		// form to add new course
		Paragraph courseFormTitle = new Paragraph("Add new course");
		FormLayout courseForm = new FormLayout();
		TextField courseName = new TextField("Course name");
		TextField courseShortName = new TextField("Course short name");
		TextField courseKey = new TextField("Course key");
		TextField courseBannerUrl = new TextField("Course banner URL");
		courseForm.add(courseName, courseShortName, courseKey, courseBannerUrl);
		Button addCourse = new Button("Add");

		// form to add new role
		Paragraph roleFormTitle = new Paragraph("Add new role");
		FormLayout roleForm = new FormLayout();
		TextField roleName = new TextField("Role name");
		Select<String> roleCourse = new Select<>();

		List<Course> courses = db.gatAllCourses();
		for (Course course : courses) {
			roleCourse.add(course.getName());
		}

		roleForm.add(roleName, roleCourse);
		Button addRole = new Button("Add");
		
		add(courseFormTitle, courseForm, addCourse);
		add(roleFormTitle, roleForm, addRole);

	}
}
