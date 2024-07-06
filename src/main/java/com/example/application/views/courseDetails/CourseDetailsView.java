package com.example.application.views.courseDetails;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.application.data.entity.Course;
import com.example.application.data.entity.CourseContentClasses.Panel;
import com.example.application.services.DbService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@Route(value = "details", layout = MainLayout.class)
@PermitAll
public class CourseDetailsView extends VerticalLayout implements HasDynamicTitle, HasUrlParameter<Long> {
	private DbService db;
	private Course course;

	@Override
	public String getPageTitle() {
	  return "Course: " + course.getName();
	}

	@Override
	public void setParameter(BeforeEvent event, Long parameter) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String requiredRole = "ROLE_COURSE_" + parameter;

		boolean hasRole = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.anyMatch((role) -> role.equals(requiredRole) || role.equals("ROLE_ADMIN"));

		if (!hasRole) {
			throw new AccessDeniedException("Access to this resource was denied");
		}

		course = db.getCourse(parameter);
		UI.getCurrent().getInternals().setTitle(getPageTitle());

		constructUI();
	}
	
	public CourseDetailsView(DbService db) {
		this.db = db;
	}

	private void constructUI() {
		removeAll();

		// current user is owner of the course
		boolean canEdit = course.getOwner().equals(db.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName()));

		VerticalLayout panelContainer = new VerticalLayout();

		for (Panel panel : course.getPanels()) {
			panelContainer.add(new CoursePanel(panel, canEdit));
		}

		Panel test = new Panel();
		test.setTitle("Test");
		test.setContent("Test content");
		
		panelContainer.add(new CoursePanelEditable(test, canEdit));
		panelContainer.add(new CoursePanel(test, canEdit));
		panelContainer.add(new CoursePanel(test, canEdit));

		if (canEdit) {
			panelContainer.add(new PanelAddButton());
		}

		add(panelContainer);
	}
}