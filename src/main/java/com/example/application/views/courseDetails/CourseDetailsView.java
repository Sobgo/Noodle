package com.example.application.views.courseDetails;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.application.data.entity.CourseClasses.Course;
import com.example.application.data.entity.CourseClasses.Panel;
import com.example.application.services.DbService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
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

	private Map<Long, CoursePanel> panels = new HashMap<>();

	private Integer lastOrderNr = 0;

	@Override
	public String getPageTitle() {
	  return "Course: " + course.getName();
	}

	@Override
	@PreAuthorize("@userAuthorizationService.isAuthorizedDetails(#parameter)")
	public void setParameter(BeforeEvent event, Long parameter) {
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

		BiConsumer<Long, Boolean> editCallback = (id, edited) -> {
			if (canEdit) {
				if (edited) {
					Panel panel = panels.get(id).getPanel();
					course = db.addPanel(course.getId(), panel);
				} else {
					course = db.deletePanel(course.getId(), id);
				}

				constructUI();
			}
		};

		List<Panel> P = course.getPanels();
		P.sort(Comparator.comparing(Panel::getOrderIndex));

		for (Panel panel : P) {
			CoursePanel coursePanel = new CoursePanel(panel, canEdit, editCallback);
			panels.put(panel.getId(), coursePanel);
			panelContainer.add(coursePanel);
			lastOrderNr = Math.max(lastOrderNr, panel.getOrderIndex());
		}

		if (canEdit) {
			panelContainer.add(createAddPanelButton());
		}

		add(panelContainer);
	}

	private Div createAddPanelButton() {
		Div container = new Div();

		container.setWidth("100%");
		container.getStyle().set("box-sizing", "border-box");
		container.getStyle().set("border", "1px dashed var(--lumo-contrast-20pct)");
		container.getStyle().set("color", "var(--lumo-contrast-50pct)");
		container.getStyle().set("border-radius", "5px");
		container.getStyle().set("padding", "10px");
		container.getStyle().set("padding-left", "20px");
		container.getStyle().set("display", "flex");
		container.getStyle().set("justify-content", "center");
		container.getStyle().set("align-items", "center");
		container.getStyle().set("cursor", "pointer");

		container.addClickListener((event) -> {
			lastOrderNr++;
			course = db.addPanel(course.getId(), new Panel(lastOrderNr));
			constructUI();
		});

		Icon addIcon = new Icon("lumo", "plus");
		container.add(addIcon);

		return container;
	}
}