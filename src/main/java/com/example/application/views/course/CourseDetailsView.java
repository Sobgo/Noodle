package com.example.application.views.course;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.application.data.entity.Course.Course;
import com.example.application.data.entity.Course.Panel;
import com.example.application.services.DbService;
import com.example.application.services.GlobalAccessService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@Route(value = "details", layout = MainLayout.class)
@PermitAll
public class CourseDetailsView extends VerticalLayout
		implements HasDynamicTitle, HasUrlParameter<Long>, BeforeLeaveObserver {
	private DbService db;
	private GlobalAccessService globalAccessService;

	private Course course;
	private Icon courseEditIcon = new Icon("lumo", "cog");

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

	@Override
	public void beforeLeave(BeforeLeaveEvent event) {
		globalAccessService.getMainLayout().remove(courseEditIcon);
	}

	public CourseDetailsView(DbService db, GlobalAccessService globalAccessService) {
		this.db = db;
		this.globalAccessService = globalAccessService;
	}

	private void constructUI() {
		removeAll();
		globalAccessService.getMainLayout().remove(courseEditIcon);

		// current user is owner of the course
		boolean canEdit = course.getOwner()
				.equals(db.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName()));

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

			MainLayout mainLayout = globalAccessService.getMainLayout();

			courseEditIcon = new Icon("lumo", "cog");
			courseEditIcon.getStyle()
					.set("cursor", "pointer")
					.set("margin-left", "auto")
					.set("margin-right", "10px");

			courseEditIcon.addClickListener((event) -> {
				UI.getCurrent().navigate("edit/" + course.getId());
			});

			mainLayout.addToNavbar(courseEditIcon);
		}

		add(panelContainer);
	}

	private Div createAddPanelButton() {
		Div container = new Div();

		container.setWidth("100%");
		container.getStyle()
				.set("box-sizing", "border-box")
				.set("border", "1px dashed var(--lumo-contrast-20pct)")
				.set("color", "var(--lumo-contrast-50pct)")
				.set("border-radius", "5px")
				.set("padding", "10px")
				.set("padding-left", "20px")
				.set("display", "flex")
				.set("justify-content", "center")
				.set("align-items", "center")
				.set("cursor", "pointer");

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