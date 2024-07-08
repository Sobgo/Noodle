package com.example.application.views.controls;

import java.io.ByteArrayInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.application.data.entity.Role;
import com.example.application.data.entity.User;
import com.example.application.data.entity.Course.Course;
import com.example.application.services.DbService;
import com.example.application.views.MainLayout;
import com.example.application.views.home.CourseCard;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

import jakarta.annotation.security.RolesAllowed;

@PageTitle("Admin Panel")
@Route(value = "admin", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AdminView extends VerticalLayout {

	@Autowired
	private UserDetailsService userDetailsService;
	
	private DbService db;

	private Course course = new Course();
	private HorizontalLayout cardContainer;
	
	public AdminView(DbService db) {
		this.db = db;
		constructUI();
		updatePreview();
	}

	private boolean addCourse() {
		if (course.getName() == null || course.getName().isEmpty()) {
			return false;
		}

		if (course.getOwner() == null) {
			return false;
		}

		Role courseRole = new Role();
		courseRole.setName("COURSE_PLACEHOLDER");
		course.setCourseRole(courseRole);
		course.setVisible(true);

		Role dbRole = db.saveRole(courseRole);
		Course dbCourse = db.saveCourse(course);

		// update name to match course id
		dbRole.setName("COURSE_" + dbCourse.getId());
		db.saveRole(dbRole);

		// grant role to owner
		db.grantRole(course.getOwner().getId(), dbRole.getId());

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		// if user that triggered this is same as owner, refresh session authorities
		if (auth.getName().equals(course.getOwner().getUsername())) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(auth.getName());
			Authentication newAuth = new UsernamePasswordAuthenticationToken(
				userDetails,
				auth.getCredentials(),
				userDetails.getAuthorities()
			);

			SecurityContextHolder.getContext().setAuthentication(newAuth);
		}

		return true;
	}

	private void updatePreviewTextOnly() {
		cardContainer.getChildren().forEach(card -> {
			CourseCard viewCard = (CourseCard) card;
			viewCard.updateTitle(course.getName());
		});
	}

	private void updatePreview() {	
		byte[] bytes = course.getBanner();
		StreamResource banner;

		if (bytes == null) {
			String path = "../../img/defaultBanner.png";
			banner = new StreamResource("default", () -> getClass().getResourceAsStream(path));
		} else {
			banner = new StreamResource("banner", () -> new ByteArrayInputStream(course.getBanner()));
		}

		CourseCard cardPreview = new CourseCard(0L, course.getName(), banner);
		cardContainer.removeAll();
		cardContainer.add(cardPreview);
	}

	private void constructUI() {
		HorizontalLayout wrapper = new HorizontalLayout();
		wrapper.setJustifyContentMode(JustifyContentMode.CENTER);
		wrapper.setWidth("100%");
		wrapper.getStyle().set("flex-wrap", "wrap");
		wrapper.getStyle().set("flex-direction", "row-reverse");
			
			VerticalLayout form = new VerticalLayout();
			form.setMaxWidth("400px");
			form.setWidth("100%");
			form.setAlignItems(Alignment.STRETCH);
			form.getStyle().set("border-radius", "var(--lumo-border-radius-m)");
			form.getStyle().set("background-color", "var(--lumo-contrast-5pct)");
			form.getStyle().set("padding", "var(--lumo-space-s)");
			form.setSpacing(false);

				H3 pageTitle = new H3("Configure new course");

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
						StreamResource resource = new StreamResource("profile-pic",
							() -> new ByteArrayInputStream(profilePicture));
						avatar.setImageResource(resource);
					}
		
					avatar.setWidth("var(--lumo-size-m)");
					avatar.getStyle().set("margin-right", "var(--lumo-space-s)");
		
					flexWrapper.add(avatar);
					flexWrapper.add(user.getUsername());
					return flexWrapper;
				}));

				userSelect.addValueChangeListener(e -> {
					course.setOwner(e.getValue());
				});

				Image test = new Image();

				Div courseBanner = new Div();
					Span courseBannerLabel = new Span("Course banner");
					courseBanner.add(courseBannerLabel);
					courseBanner.addClassName(Margin.Top.MEDIUM);

					Upload courseBannerInput = new Upload();
					courseBannerInput.setAcceptedFileTypes("image/*");
					courseBannerInput.setMaxFiles(1);
					courseBannerInput.setMaxFileSize(10 * 1024 * 1024);
					courseBannerInput.setDropAllowed(true);

					courseBannerInput.getStyle().set("margin", "0");

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
			
				Button addCourse = new Button("Add course", e -> {
					if (!addCourse()) {
						Notification notification = new Notification();
						notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
						notification.setPosition(Notification.Position.TOP_CENTER);

						Div text = new Div(new Text("Invalid or empty course name or owner"));

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
			form.add(pageTitle, courseNameInput, userSelect, courseKeyInput, courseBanner, test, addCourse);

			cardContainer = new HorizontalLayout();
			cardContainer.setJustifyContentMode(JustifyContentMode.CENTER);

			VerticalLayout cardWrapper = new VerticalLayout();
			cardWrapper.setMaxWidth("400px");
			cardWrapper.setPadding(false);
			cardWrapper.add("Preview:");
			cardWrapper.add(cardContainer);

		wrapper.add(cardWrapper, form);

		add(wrapper);
	}
}
