package com.example.application.views.courseDetails;

import com.example.application.data.entity.CourseContentClasses.Panel;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.theme.lumo.LumoIcon;

public class CoursePanel extends Div {
	Panel panel;

	public CoursePanel(Panel panel, boolean canEdit) {
		this.panel = panel;
		constructUI(canEdit);
	}

	public void constructUI(boolean canEdit) {
		setWidth("100%");
		getStyle().set("box-sizing", "border-box");
		getStyle().set("border-radius", "5px");
		getStyle().set("padding", "10px");
		getStyle().set("padding-left", "20px");
		getStyle().set("background-color", "var(--lumo-contrast-5pct)");
		
		// title
		Span title = new Span("title");
		Div titleContainer = new Div(title);
		
		Details panelDetails = new Details(titleContainer);
		panelDetails.setOpened(true);
		panelDetails.getSummary().getStyle().set("font-size", "1.2em");
		panelDetails.getSummary().getStyle().set("padding-left", "5px");
		panelDetails.getSummary().getStyle().set("color", "var(--lumo-body-text-color)");
		
		// content 
		Span panelContent = new Span();
		panelContent.getElement().setProperty("innerHTML", "content");

		panelDetails.add(panelContent);

		Div editIconContainer = new Div();
		editIconContainer.setWidth("100%");
		editIconContainer.getStyle().set("display", "flex");
		editIconContainer.getStyle().set("justify-content", "flex-end");
		editIconContainer.getStyle().set("padding", "0");
		editIconContainer.getStyle().set("margin", "0");
		editIconContainer.getStyle().set("margin-bottom", "-20px");

		Icon removeIcon = LumoIcon.MINUS.create();
		removeIcon.setSize("20px");
		removeIcon.getStyle().set("cursor", "pointer");

		removeIcon.addSingleClickListener((event) -> {
			// remove panel
		});

		Icon editIcon = LumoIcon.EDIT.create();
		editIcon.setSize("20px");
		editIcon.getStyle().set("cursor", "pointer");

		editIcon.addSingleClickListener((event) -> {
			// edit panel
		});

		editIconContainer.add(editIcon, removeIcon);

		if (canEdit) {
			add(editIconContainer);
		}
		
		add(panelDetails);
	}
}
