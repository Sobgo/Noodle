package com.example.application.views.courseDetails;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;

public class PanelAddButton extends Div {
	public PanelAddButton() {
		constructUI();
	}

	private void constructUI() {
		setWidth("100%");
		getStyle().set("box-sizing", "border-box");
		getStyle().set("border", "1px dashed var(--lumo-contrast-20pct)");
		getStyle().set("color", "var(--lumo-contrast-50pct)");
		getStyle().set("border-radius", "5px");
		getStyle().set("padding", "10px");
		getStyle().set("padding-left", "20px");
		getStyle().set("display", "flex");
		getStyle().set("justify-content", "center");
		getStyle().set("align-items", "center");
		getStyle().set("cursor", "pointer");

		addClickListener((event) -> {
			// add panel
		});

		Icon addIcon = new Icon("lumo", "plus");
		add(addIcon);
	}
}
