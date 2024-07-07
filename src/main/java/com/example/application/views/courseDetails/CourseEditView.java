package com.example.application.views.courseDetails;

import java.io.ByteArrayInputStream;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;

import com.example.application.data.entity.Role;
import com.example.application.data.entity.User;
import com.example.application.data.entity.CourseClasses.Course;
import com.example.application.services.DbService;
import com.example.application.views.MainLayout;
import com.example.application.views.courses.CoursesViewCard;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

import jakarta.annotation.security.PermitAll;

@Route(value = "edit", layout = MainLayout.class)
@PageTitle("Edit Course")
@PermitAll
public class CourseEditView extends VerticalLayout implements HasUrlParameter<Long> {	
	private DbService db;

	private Course course = new Course();
	private HorizontalLayout cardContainer;
	private Scroller scroller;

	@Override
	@PreAuthorize("@userAuthorizationService.isAuthorizedEdit(#parameter)")
	public void setParameter(BeforeEvent event, Long parameter) {
		course = db.getCourse(parameter);
		constructUI();
		updatePreview();
	}
	
	public CourseEditView(DbService db) {
		this.db = db;
	}

	private boolean saveCourse() {
		if (course.getName() == null || course.getName().isEmpty()) {
			return false;
		}

		if (course.getOwner() == null) {
			return false;
		}

		if (course.getKey() != null && course.getKey().isEmpty()) {
			course.setKey(null);
		}

		db.saveCourse(course);	
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
			String path = "../../img/default.png";
			banner = new StreamResource("default", () -> getClass().getResourceAsStream(path));
		} else {
			banner = new StreamResource("banner", () -> new ByteArrayInputStream(course.getBanner()));
		}

		CoursesViewCard cardPreview = new CoursesViewCard(0L, course.getName(), banner);
		cardContainer.removeAll();
		cardContainer.add(cardPreview);
	}

	private void constructUI() {
		FormLayout wrapper = new FormLayout();

			FormLayout form = new FormLayout();
			form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
			form.setMaxWidth("500px");

				TextField courseNameInput = new TextField("Course name");
				courseNameInput.setRequired(true);
				courseNameInput.setRequiredIndicatorVisible(true);
				courseNameInput.setValue(course.getName() == null ? "" : course.getName());
				courseNameInput.setValueChangeMode(ValueChangeMode.EAGER);
		
				courseNameInput.addValueChangeListener(e -> {
					course.setName(e.getValue());
					updatePreviewTextOnly();
				});
		
				TextField courseKeyInput = new TextField("Course key");
				courseKeyInput.setPlaceholder("Leave empty for no key");
				courseKeyInput.setValue(course.getKey() == null ? "" : course.getKey());

				courseKeyInput.addValueChangeListener(e -> {
					course.setKey(e.getValue());
				});

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
			
				Button addCourse = new Button("Save course", e -> {
					if (!saveCourse()) {
						// display error message
						Notification notification = new Notification();
						notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
						notification.setPosition(Notification.Position.TOP_CENTER);

						Div text = new Div(new Text("Failed to add course"));

						Button closeButton = new Button(new Icon("lumo", "cross"));
						closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
						closeButton.setAriaLabel("Close");
						
						closeButton.addClickListener(event -> {
							notification.close();
						});

						HorizontalLayout layout = new HorizontalLayout(text, closeButton);
						layout.setAlignItems(Alignment.CENTER);

						notification.add(layout);
						notification.open();
					} else {
						// redirect to course page
						UI.getCurrent().navigate("details/" + course.getId());
					}
				});
				addCourse.addClassName(Margin.Top.LARGE);

				Button deleteCourse = new Button("Delete course", e -> {
					Dialog confirmDialog = new Dialog();
					confirmDialog.add(new Span("Are you sure you want to delete this course?"));

					Button confirmButton = new Button("Yes");
					confirmButton.addClickListener((event) -> {
						db.deleteCourse(course.getId());
						confirmDialog.close();
						UI.getCurrent().navigate("");
					});

					Button cancelButton = new Button("No");
					cancelButton.addClickListener((event) -> {
						confirmDialog.close();
					});

					confirmDialog.getFooter().add(confirmButton, cancelButton);
					confirmDialog.open();
				});
				deleteCourse.addClassName(Margin.Top.LARGE);
				deleteCourse.addThemeVariants(ButtonVariant.LUMO_ERROR);
				
			form.add(courseNameInput, courseKeyInput, courseBanner, addCourse, deleteCourse);

			cardContainer = new HorizontalLayout();
			cardContainer.setJustifyContentMode(JustifyContentMode.CENTER);

			VerticalLayout cardWrapper = new VerticalLayout();
			cardWrapper.addClassName(Margin.Top.LARGE);
			cardWrapper.add("Preview:");
			cardWrapper.add(cardContainer);

			cardWrapper.add("Course users:");
			scroller = createScroller();
			cardWrapper.add(scroller);

		wrapper.add(form, cardWrapper);

		add(wrapper);
	}

	private Scroller createScroller() {
		Role role = course.getCourseRole();
		Set<User> users = role.getUsers();

		VerticalLayout userContainer = new VerticalLayout();
		userContainer.setWidth("100%");

		for (User user : users) {
			HorizontalLayout userDiv = new HorizontalLayout();
			userDiv.setWidth("100%");
			userDiv.setAlignItems(Alignment.CENTER);
			userDiv.setJustifyContentMode(JustifyContentMode.BETWEEN);
			userDiv.getStyle().set("border-radius", "var(--lumo-border-radius-m)");
			userDiv.getStyle().set("background-color", "var(--lumo-contrast-5pct)");
			userDiv.getStyle().set("padding", "var(--lumo-space-s)");

			Icon revokeIcon;

			if (course.getOwner().getId() == user.getId()) {
				revokeIcon = new Icon("lumo", "check");
			} else {
				revokeIcon = new Icon("lumo", "cross");
				revokeIcon.addClickListener(e -> {
					Dialog confirmDialog = new Dialog();
					confirmDialog.add(new Span("Are you sure you want to revoke access for this user?"));

					Button confirmButton = new Button("Yes");
					confirmButton.addClickListener((event) -> {
						db.revokeRole(user.getId(), role.getId());
						confirmDialog.close();

						scroller.getChildren().forEach(child -> {
							if (child instanceof VerticalLayout) {
								VerticalLayout layout = (VerticalLayout) child;
								layout.remove(userDiv);
							}
						});
					});

					Button cancelButton = new Button("No");
					cancelButton.addClickListener((event) -> {
						confirmDialog.close();
					});

					confirmDialog.getFooter().add(confirmButton, cancelButton);
					confirmDialog.open();
				});

				revokeIcon.getStyle().set("color", "var(--lumo-error-color)");
				revokeIcon.getStyle().set("cursor", "pointer");
			}

			Avatar avatar = new Avatar(user.getUsername());
			byte[] profilePicture = user.getProfilePicture();

			if (profilePicture != null) {
				// TODO: Fix this when profile pictures are implemented
				avatar.setImage("data:image/png;base64," + profilePicture);
			}

			avatar.setWidth("var(--lumo-size-m)");
			avatar.getStyle().set("margin-right", "var(--lumo-space-s)");

			HorizontalLayout userProfileContainer = new HorizontalLayout();
			userProfileContainer.setAlignItems(Alignment.CENTER);
			userProfileContainer.getStyle().set("padding", "0");
			userProfileContainer.getStyle().set("margin", "0");
			userProfileContainer.setSpacing(false);

			userProfileContainer.add(avatar);
			userProfileContainer.add(user.getUsername());

			userDiv.add(userProfileContainer);
			userDiv.add(revokeIcon);

			userContainer.add(userDiv);
		}

		Scroller wrapper = new Scroller(userContainer);
		wrapper.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
		wrapper.getStyle()
			.set("border", "1px solid var(--lumo-contrast-20pct)")
			.set("border-radius", "var(--lumo-border-radius-m)")
			.set("box-sizing", "border-box");

		wrapper.setHeight("200px");
		wrapper.setWidth("300px");

		return wrapper;
	}
}
