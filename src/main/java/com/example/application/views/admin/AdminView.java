package com.example.application.views.admin;

import java.io.ByteArrayInputStream;

import com.example.application.data.entity.Course;
import com.example.application.data.entity.User;
import com.example.application.services.DbService;
import com.example.application.views.MainLayout;
import com.example.application.views.courses.CoursesViewCard;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "admin", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AdminView extends VerticalLayout {
	
	private DbService db;

	private Course course = new Course();
	private HorizontalLayout cardContainer;
	
	public AdminView(DbService db) {
		this.db = db;
		constructUI();
		updatePreview();
	}

	private boolean addCourse() {
		// TODO
		return true;
	}

	private void updatePreviewTextOnly() {
		cardContainer.getChildren().forEach(card -> {
			CoursesViewCard viewCard = (CoursesViewCard) card;
			viewCard.updateTitle(course.getName());
		});
	}

	private void updatePreview() {	
		byte[] bytes = course.getBanner();
		StreamResource banner;

		if (bytes == null) {
			String path = "../../img/default.jpg";
			banner = new StreamResource("default.jpg", () -> getClass().getResourceAsStream(path));
		} else {
			banner = new StreamResource("banner", () -> new ByteArrayInputStream(course.getBanner()));
		}

		CoursesViewCard cardPreview = new CoursesViewCard(0L, course.getName(), banner, false);
		cardContainer.removeAll();
		cardContainer.add(cardPreview);
	}

	private void constructUI() {
		H3 pageTitle = new H3("Configure new course");

		FormLayout wrapper = new FormLayout();
			
			FormLayout form = new FormLayout();
			form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
			form.setMaxWidth("500px");

				TextField courseNameInput = new TextField("Course name");
				courseNameInput.setRequired(true);
				courseNameInput.setRequiredIndicatorVisible(true);

				courseNameInput.setValueChangeMode(ValueChangeMode.EAGER);
		
				courseNameInput.addValueChangeListener(e -> {
					course.setName(e.getValue());
					updatePreviewTextOnly();
				});
		
				TextField courseKeyInput = new TextField("Course key");
				courseKeyInput.setPlaceholder("Leave empty for no key");

				courseKeyInput.addValueChangeListener(e -> {
					course.setKey(e.getValue());
				});

				Select<User> userSelect = new Select<>();
				userSelect.setLabel("Select course owner");
				userSelect.setItems(db.getUsers());
				userSelect.setEmptySelectionAllowed(false);
				userSelect.setPlaceholder("Select user");

				userSelect.setRenderer(new ComponentRenderer<>(user -> {
					FlexLayout flexWrapper = new FlexLayout();
					flexWrapper.setAlignItems(Alignment.CENTER);
		
					Avatar avatar = new Avatar(user.getUsername());
					byte[] profilePicture = user.getProfilePicture();
		
					if (profilePicture != null) {
						// TODO: Fix this when profile pictures are implemented
						avatar.setImage("data:image/png;base64," + profilePicture);
					}
		
					avatar.setWidth("var(--lumo-size-m)");
					avatar.getStyle().set("margin-right", "var(--lumo-space-s)");
		
					flexWrapper.add(avatar);
					flexWrapper.add(user.getUsername());
					return flexWrapper;
				}));

				Image test = new Image();

				Div courseBanner = new Div();
					Span courseBannerLabel = new Span("Course banner");
					courseBanner.add(courseBannerLabel);
					courseBanner.addClassName(Margin.Top.MEDIUM);

					Upload courseBannerInput = new Upload();
					courseBannerInput.setAcceptedFileTypes("image/jpg", "image/png");
					courseBannerInput.setMaxFiles(1);
					courseBannerInput.setMaxFileSize(10 * 1024 * 1024);
					courseBannerInput.setDropAllowed(true);

					MemoryBuffer memoryBuffer = new MemoryBuffer();
					courseBannerInput.setReceiver(memoryBuffer);
					courseBannerInput.addSucceededListener(event -> {
						byte[] bytes;

						try {
							bytes = memoryBuffer.getInputStream().readAllBytes();
						} catch (Exception e) {
							bytes = null;
						}

						course.setBanner(bytes);
						updatePreview();
					});

					courseBannerInput.getElement().addEventListener("file-remove", e -> {
						course.setBanner(null);
						updatePreview();
					});
				courseBanner.add(courseBannerInput);
			
				Button addCourse = new Button("Add course", e -> addCourse());
				addCourse.addClassName(Margin.Top.LARGE);
			form.add(courseNameInput, userSelect, courseKeyInput, courseBanner, test, addCourse);

			cardContainer = new HorizontalLayout();
			cardContainer.setJustifyContentMode(JustifyContentMode.CENTER);

			VerticalLayout cardWrapper = new VerticalLayout();
			cardWrapper.addClassName(Margin.Top.LARGE);
			cardWrapper.add("Preview:");
			cardWrapper.add(cardContainer);

		wrapper.add(form, cardWrapper);

		add(pageTitle, wrapper);
	}
}
