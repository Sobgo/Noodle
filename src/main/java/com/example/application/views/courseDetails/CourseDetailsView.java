package com.example.application.views.courseDetails;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.application.data.entity.Course;
import com.example.application.data.entity.CourseContentClasses.Panel;
import com.example.application.services.DbService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@Route(value = "details", layout = MainLayout.class)
@PermitAll
public class CourseDetailsView extends VerticalLayout implements HasUrlParameter<Long> {

	private DbService db;

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

		constructUI(parameter);
	}
	
	public CourseDetailsView(DbService db) {
		this.db = db;
	}

	private void constructUI(Long id) {
		Span span = new Span("Course details " + id);

		VerticalLayout panelContainer = new VerticalLayout();

		Course c = db.getCourse(id);
		if (c == null) return;

		List<Panel> panels = c.getPanels();
		
		for (Panel panel : panels) {
			panelContainer.add(new CoursePanel(panel));
		}

		add(span);
	}
}
