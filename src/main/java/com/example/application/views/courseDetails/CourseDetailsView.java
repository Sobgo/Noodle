package com.example.application.views.courseDetails;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

@Route(value = "details", layout = MainLayout.class)
public class CourseDetailsView extends VerticalLayout implements HasUrlParameter<Long> {

	@Override
	public void setParameter(BeforeEvent event, Long parameter) {
		constructUI(parameter);
	}
	
	public CourseDetailsView() {};

	private void constructUI(Long id) {
		Span span = new Span("Course details " + id);

		add(span);
	}
}
