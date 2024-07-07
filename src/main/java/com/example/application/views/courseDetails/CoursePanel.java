package com.example.application.views.courseDetails;

import java.util.function.BiConsumer;

import com.example.application.data.entity.CourseClasses.Panel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoIcon;

import lombok.Getter;

public class CoursePanel extends Div {
	@Getter
	Panel panel;

	boolean canEdit;
	boolean editMode = false;
	BiConsumer<Long, Boolean> editCallback;

	public CoursePanel(Panel panel, boolean canEdit, BiConsumer<Long, Boolean> editCallback) {
		this.panel = panel;
		this.canEdit = canEdit;
		this.editCallback = editCallback;
		constructUI();
	}

	public void constructUI() {
		removeAll();

		setWidth("100%");
		getStyle().set("box-sizing", "border-box");
		getStyle().set("border-radius", "5px");
		getStyle().set("padding", "10px 20px 0px 20px");
		getStyle().set("background-color", "var(--lumo-contrast-5pct)");

		Div titleContainer = new Div();

		Details panelDetails = new Details(titleContainer);
		panelDetails.setOpened(true);
		panelDetails.getSummary().getStyle().set("font-size", "1.2em");
		panelDetails.getSummary().getStyle().set("padding-left", "5px");
		panelDetails.getSummary().getStyle().set("color", "var(--lumo-body-text-color)");

		panelDetails.getSummary().getElement()
			.addEventListener("click", e -> {})
			.addEventData("event.stopPropagation()");

		panelDetails.getSummary().getElement()
			.addEventListener("keydown", e -> {})
			.addEventData("event.stopPropagation()");


		Div editIconContainer = new Div();
		editIconContainer.setWidth("100%");
		editIconContainer.getStyle().set("display", "flex");
		editIconContainer.getStyle().set("justify-content", "flex-end");
		editIconContainer.getStyle().set("padding", "0");
		editIconContainer.getStyle().set("margin", "0");
		if (canEdit) editIconContainer.getStyle().set("margin-bottom", "-20px");

		Icon removeIcon = LumoIcon.MINUS.create();
		removeIcon.setSize("20px");
		removeIcon.getStyle().set("cursor", "pointer");

		removeIcon.addSingleClickListener((event) -> {
			Dialog confirmDialog = new Dialog();
			confirmDialog.add(new Span("Are you sure you want to remove this panel?"));

			Button confirmButton = new Button("Yes");
			confirmButton.addClickListener((e) -> {
				editCallback.accept(panel.getId(), false);
				confirmDialog.close();
			});

			Button cancelButton = new Button("No");
			cancelButton.addClickListener((e) -> {
				confirmDialog.close();
			});

			confirmDialog.getFooter().add(confirmButton, cancelButton);
			confirmDialog.open();
		});
		
		if (editMode) {	
			TextField title = new TextField();
			title.setValue(panel.getTitle());
			titleContainer.add(title);

			TextArea panelContent = new TextArea();
			panelContent.setValue(panel.getContent());
			panelContent.setWidth("100%");
			panelContent.getStyle().set("box-sizing", "border-box");
			panelDetails.add(panelContent);

			Icon confirmIcon = LumoIcon.CHECKMARK.create();
			confirmIcon.setSize("20px");
			confirmIcon.getStyle().set("cursor", "pointer");
	
			confirmIcon.addSingleClickListener((event) -> {
				if (canEdit) {
					panel.setTitle(title.getValue());
					panel.setContent(panelContent.getValue());
					editCallback.accept(panel.getId(), true);
				}
				editMode = false;
				constructUI();
			});

	
			Icon rejectIcon = LumoIcon.CROSS.create();
			rejectIcon.setSize("20px");
			rejectIcon.getStyle().set("cursor", "pointer");
	
			rejectIcon.addSingleClickListener((event) -> {
				editMode = false;
				constructUI();
			});

			editIconContainer.add(confirmIcon, rejectIcon);
		} else {
			Span title = new Span(panel.getTitle());
			titleContainer.add(title);

			Span panelContent = new Span();
			panelContent.getElement().setProperty("innerHTML", panel.getContent());
			panelDetails.add(panelContent);


			Icon editIcon = LumoIcon.EDIT.create();
			editIcon.setSize("20px");
			editIcon.getStyle().set("cursor", "pointer");
	
			editIcon.addSingleClickListener((event) -> {
				editMode = true;
				constructUI();
			});

			if (canEdit) editIconContainer.add(editIcon);
		}

		if (canEdit) editIconContainer.add(removeIcon);
		add(editIconContainer);
		add(panelDetails);
	}
}
