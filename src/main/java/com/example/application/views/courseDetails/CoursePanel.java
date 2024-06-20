package com.example.application.views.courseDetails;

import com.example.application.data.entity.CourseContentClasses.Panel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class CoursePanel extends VerticalLayout {

	public CoursePanel(Panel panel) {
		constructUI();
	}

	public void constructUI() {
		Span span = new Span("Panel");
		add(span);
	}
}
