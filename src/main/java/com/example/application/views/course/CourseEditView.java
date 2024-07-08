package com.example.application.views.course;

import java.io.ByteArrayInputStream;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;

import com.example.application.data.entity.Role;
import com.example.application.data.entity.User;
import com.example.application.data.entity.Course.Course;
import com.example.application.services.DbService;
import com.example.application.services.GlobalAccessService;
import com.example.application.views.MainLayout;
import com.example.application.views.home.CourseCard;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
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
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

import jakarta.annotation.security.PermitAll;

@Route(value = "edit", layout = MainLayout.class)
@PageTitle("Edit Course")
@PermitAll
public class CourseEditView extends VerticalLayout implements HasUrlParameter<Long>, BeforeLeaveObserver {	
	private DbService db;
	private GlobalAccessService globalAccessService;

	private Course course = new Course();
	private HorizontalLayout cardContainer;
	private Scroller scroller;
	private Icon backIcon = new Icon("lumo", "arrow-left");
	private byte[] bannerImg;

	@Override
	@PreAuthorize("@userAuthorizationService.isAuthorizedEdit(#parameter)")
	public void setParameter(BeforeEvent event, Long parameter) {
		course = db.getCourse(parameter);
		bannerImg = course.getBanner();
		constructUI();
		updatePreview();
	}

	@Override
	public void beforeLeave(BeforeLeaveEvent event) {
		globalAccessService.getMainLayout().remove(backIcon);
	}
	
	public CourseEditView(DbService db, GlobalAccessService globalAccessService) {
		this.db = db;
		this.globalAccessService = globalAccessService;
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

		if (bannerImg != null) {
			course.setBanner(bannerImg);
		}

		db.saveCourse(course);	
		return true;
	}

	private void updatePreviewTextOnly() {
		cardContainer.getChildren().forEach(card -> {
			CourseCard viewCard = (CourseCard) card;
			viewCard.updateTitle(course.getName());
		});
	}

	private void updatePreview() {	
		StreamResource banner;
		byte[] bytes = (bannerImg == null) ? course.getBanner() : bannerImg;

		if (bytes == null) {
			String path = "../../img/defaultBanner.png";
			banner = new StreamResource("default", () -> getClass().getResourceAsStream(path));
		} else {
			banner = new StreamResource("banner", () -> new ByteArrayInputStream(bytes));
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
		wrapper.setAlignItems(Alignment.START);
		wrapper.setJustifyContentMode(JustifyContentMode.CENTER);

			VerticalLayout form = new VerticalLayout();
			form.setMaxWidth("400px");
			form.setWidth("100%");
			form.setAlignItems(Alignment.STRETCH);
			form.getStyle().set("border-radius", "var(--lumo-border-radius-m)");
			form.getStyle().set("background-color", "var(--lumo-contrast-5pct)");
			form.getStyle().set("padding", "var(--lumo-space-s)");
			form.setSpacing(false);

				H3 pageTitle = new H3("Configure Course");

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
					courseBannerInput.setAcceptedFileTypes("image/*");
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

						bannerImg = bytes;
						updatePreview();
					});

					courseBannerInput.getElement().addEventListener("file-remove", e -> {
						bannerImg = null;
						updatePreview();
					});

					Button removeBanner = new Button("Remove banner", e -> {
						bannerImg = null;
						course.setBanner(null);
						updatePreview();
					});
					removeBanner.addThemeName("error secondary");

				courseBanner.add(courseBannerInput, removeBanner);
			
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
				deleteCourse.addThemeVariants(ButtonVariant.LUMO_ERROR);
				
			form.add(pageTitle, courseNameInput, courseKeyInput, courseBanner, addCourse, deleteCourse);

			cardContainer = new HorizontalLayout();
			cardContainer.setJustifyContentMode(JustifyContentMode.CENTER);

			VerticalLayout cardWrapper = new VerticalLayout();
			cardWrapper.add("Preview:");
			cardWrapper.setMaxWidth("400px");
			cardWrapper.setPadding(false);
			cardWrapper.add(cardContainer);

			VerticalLayout scrollerContainer = new VerticalLayout();
			scrollerContainer.getStyle().set("border-radius", "var(--lumo-border-radius-m)")
			.set("background-color", "var(--lumo-contrast-5pct)")
			.set("padding", "var(--lumo-space-s)");

			scroller = createScroller();
			scrollerContainer.add("Course members:");
			scrollerContainer.add(scroller);
			scrollerContainer.setWidth("100%");
			scrollerContainer.setMaxWidth("300px");
			scrollerContainer.setPadding(false);
			scrollerContainer.setMargin(false);
			scrollerContainer.setSpacing(false);
			cardWrapper.add(scrollerContainer);

		wrapper.add(cardWrapper, form);

		MainLayout mainLayout = globalAccessService.getMainLayout();

		backIcon = new Icon("lumo", "arrow-right");
		backIcon.getStyle().set("cursor", "pointer");
		backIcon.getStyle().set("margin-left", "auto");
		backIcon.getStyle().set("margin-right", "10px");
		backIcon.addClickListener((event) -> {
			UI.getCurrent().navigate("details/" + course.getId());
			mainLayout.remove(backIcon);
		});

		mainLayout.addToNavbar(backIcon);

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
                StreamResource resource = new StreamResource("profile-pic",
                    () -> new ByteArrayInputStream(profilePicture));
                 avatar.setImageResource(resource);
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
		wrapper.getStyle().set("box-sizing", "border-box");
		wrapper.getStyle().set("padding", "0");
		wrapper.setHeight("200px");
		wrapper.setWidth("100%");

		return wrapper;
	}
}
