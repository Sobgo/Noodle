package com.example.application.views.controls;

import java.io.ByteArrayInputStream;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.application.data.entity.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.DbService;
import com.example.application.services.GlobalAccessService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

import jakarta.annotation.security.PermitAll;

@PageTitle("Account Settings")
@Route(value = "account", layout = MainLayout.class)
@PermitAll
public class AccountSettingsView extends HorizontalLayout {
	private DbService db;
	private GlobalAccessService globalAccessService;
	private BCryptPasswordEncoder passwordEncoder;
	private AuthenticatedUser authenticatedUser;
	private User user;

	private Avatar avatar;
	private Span disclaimer;

	private byte[] profilePicture;

	public AccountSettingsView(DbService dbService, GlobalAccessService globalAccessService, BCryptPasswordEncoder passwordEncoder, AuthenticatedUser authenticatedUser) {
		this.db = dbService;
		this.globalAccessService = globalAccessService;
		this.passwordEncoder = passwordEncoder;
		this.authenticatedUser = authenticatedUser;

		user = db.getUserByUsername(
			SecurityContextHolder.getContext().getAuthentication().getName()
		);

		profilePicture = user.getProfilePicture();

		constructUI();
	}

	private void refreshProfilePicture() {
		if (profilePicture != null) {
			StreamResource resource = new StreamResource("profile-pic",
				() -> new ByteArrayInputStream(profilePicture));
			avatar.setImageResource(resource);
		} else {
			avatar.setImage(null);	
		}

		this.globalAccessService.getMainLayout().refreshProfilePicture();
	}

	private void constructUI() {
		getStyle().set("padding", "var(--lumo-space-m)");
		getStyle().set("flex-wrap", "wrap");
		setJustifyContentMode(JustifyContentMode.CENTER);

		VerticalLayout profileContainer = new VerticalLayout();
		VerticalLayout settingsContainer = new VerticalLayout();
		
		profileContainer.setPadding(false);
		profileContainer.setSpacing(false);
		profileContainer.setMaxWidth("450px");
		profileContainer.getStyle().set("align-self", "start");

		settingsContainer.setPadding(false);
		settingsContainer.setSpacing(false);
		settingsContainer.setMaxWidth("450px");
		settingsContainer.getStyle().set("align-self", "start");

		VerticalLayout profile = new VerticalLayout();
		profile.getStyle().set("padding", "var(--lumo-space-m)")
		.set("border-radius", "var(--lumo-border-radius-m)")
		.set("background-color", "var(--lumo-contrast-5pct)")
		.set("box-sizing", "border-box");

		// profile
		Div avatarContainer = new Div();
		avatarContainer.setWidth("100%");
		avatarContainer.setHeight("0");
		// set height to match width
		avatarContainer.getElement().getStyle().set("position", "relative");
		avatarContainer.getElement().getStyle().set("padding-bottom", "100%");
		avatarContainer.getElement().getStyle().set("overflow", "hidden");
		avatarContainer.getStyle().set("box-sizing", "border-box");

		avatar = new Avatar();	
		avatar.setWidth("100%");
		avatar.setHeight("100%");
		avatar.getStyle().set("position", "absolute");
		refreshProfilePicture();
		
		avatar.setName(user.getUsername());

		Span username = new Span(user.getUsername());
		username.getStyle().set("font-size", "2.5rem");
		username.getStyle().set("padding-left", "20px");

		disclaimer = new Span("*This is a preview. Save changes to update your profile picture.");
		disclaimer.getStyle().set("font-size", "0.8rem");
		disclaimer.getStyle().set("color", "var(--lumo-error-text-color)");
		disclaimer.setVisible(false);

		avatarContainer.add(avatar);

		profile.add(avatarContainer, disclaimer, username);

		VerticalLayout deleteAccountContainer = new VerticalLayout();

		Span deleteAccountLabel = new Span("Delete Account");
		Button deleteAccountButton = new Button("Delete Account");
		deleteAccountButton.setThemeName("error primary");

		deleteAccountButton.addClickListener(event -> {
			Dialog dialog = new Dialog();
			dialog.add("Are you sure you want to delete your account? This action cannot be undone.");
			
			Button confirmButton = new Button("Yes");
			Button cancelButton = new Button("No");

			confirmButton.setThemeName("error primary");

			confirmButton.addClickListener(e -> {
				authenticatedUser.logout();
				db.deleteUser(user.getId());
			});

			cancelButton.addClickListener(e -> {
				dialog.close();
			});

			dialog.getFooter().add(confirmButton, cancelButton);
			dialog.open();
		});

		if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"))) {
			deleteAccountButton.setEnabled(false);
			deleteAccountButton.setText("Cannot delete admin account");
		}

		deleteAccountContainer.add(deleteAccountLabel, deleteAccountButton);

		deleteAccountContainer.getStyle().set("padding", "var(--lumo-space-m)")
		.set("border-radius", "var(--lumo-border-radius-m)")
		.set("background-color", "var(--lumo-error-color-10pct)")
		.set("margin-top", "var(--lumo-space-m)")
		.set("color", "var(--lumo-error-text-color)");

		profileContainer.add(profile, deleteAccountContainer);	

		// settings
		VerticalLayout upload = new VerticalLayout();
		upload.setAlignItems(Alignment.STRETCH);

		upload.getStyle().set("padding", "var(--lumo-space-m)")
		.set("border-radius", "var(--lumo-border-radius-m)")
		.set("background-color", "var(--lumo-contrast-5pct)")
		.set("margin-bottom", "var(--lumo-space-m)");

		upload.setSpacing(false);

		Span uploadLabel = new Span("Upload Profile Picture");
		Upload uploadInput = createUpload();
		Button uploadButton = new Button("Save Picture");
		Button removeButton = new Button("Remove Current Picture");

		uploadInput.setClassName(Margin.Top.SMALL);
		uploadButton.setClassName(Margin.Top.SMALL);

		removeButton.setThemeName("error secondary");
	
		uploadButton.addClickListener(event -> {
			if (profilePicture == null) {
				Dialog dialog = new Dialog();
				dialog.add("No image uploaded.");
				Button closeButton = new Button("Close");
				closeButton.addClickListener(e -> dialog.close());

				dialog.getFooter().add(closeButton);
				dialog.open();
				return;
			}

			user.setProfilePicture(profilePicture);
			db.saveUser(user);
			refreshProfilePicture();
			disclaimer.setVisible(false);
			uploadInput.clearFileList();
		});

		removeButton.addClickListener(event -> {
			Dialog dialog = new Dialog();
			dialog.add("Are you sure you want to remove your profile picture?");
			
			Button confirmButton = new Button("Yes");
			Button cancelButton = new Button("No");

			confirmButton.addClickListener(e -> {
				profilePicture = null;
				user.setProfilePicture(null);
				db.saveUser(user);
				refreshProfilePicture();
				disclaimer.setVisible(false);
				dialog.close();
				uploadInput.clearFileList();
			});

			cancelButton.addClickListener(e -> {
				dialog.close();
			});

			dialog.getFooter().add(confirmButton, cancelButton);
			dialog.open();
		});

		upload.add(uploadLabel, uploadInput, uploadButton, removeButton);

		VerticalLayout password = new VerticalLayout();
		password.setAlignItems(Alignment.STRETCH);

		password.getStyle().set("padding", "var(--lumo-space-m)")
		.set("border-radius", "var(--lumo-border-radius-m)")
		.set("background-color", "var(--lumo-contrast-5pct)");

		Span passwordLabel = new Span("Change Password");
		PasswordField passwordInput = new PasswordField("Current Password");
		PasswordField newPasswordInput = new PasswordField("New Password");
		PasswordField repeatPasswordInput = new PasswordField("Repeat Password");
		Button changePasswordButton = new Button("Change Password");

		passwordInput.setRequired(true);
		newPasswordInput.setRequired(true);
		repeatPasswordInput.setRequired(true);

		changePasswordButton.addClickListener(event -> {
			System.out.println(passwordInput.getValue());

			String currentPassword = passwordInput.getValue();
			String newPassword = newPasswordInput.getValue();
			String repeatPassword = repeatPasswordInput.getValue();

			if (newPassword.equals(repeatPassword) && !newPassword.isEmpty()) {
				if (passwordEncoder.matches(currentPassword, user.getHashedPassword())) {
					user.setHashedPassword(passwordEncoder.encode(newPassword));

					user.setHashedPassword(passwordEncoder.encode(newPassword));
					user = db.saveUser(user);

					Notification.show("Password changed successfully.");

					passwordInput.clear();
					newPasswordInput.clear();
					repeatPasswordInput.clear();
				} else {
					Dialog dialog = new Dialog();
					dialog.add("Incorrect password.");
					Button closeButton = new Button("Close");
					closeButton.addClickListener(e -> dialog.close());

					dialog.getFooter().add(closeButton);
					dialog.open();
				}
			} else {
				Dialog dialog = new Dialog();

				if (newPassword.isEmpty() || repeatPassword.isEmpty()) {
					dialog.add("New password cannot be empty.");
				} else {
					dialog.add("Passwords do not match.");
				}
				
				Button closeButton = new Button("Close");
				closeButton.addClickListener(e -> dialog.close());

				dialog.getFooter().add(closeButton);
				dialog.open();
			}
		});

		password.add(passwordLabel, passwordInput, newPasswordInput, repeatPasswordInput, changePasswordButton);

		settingsContainer.add(upload, password);
		add(profileContainer, settingsContainer);
	}

	private Upload createUpload() {
		Upload uploadInput = new Upload();

		uploadInput.setAcceptedFileTypes("image/*");
		uploadInput.setMaxFiles(1);
		uploadInput.setMaxFileSize(10 * 1024 * 1024);
		uploadInput.setDropAllowed(true);

		MemoryBuffer memoryBuffer = new MemoryBuffer();

		uploadInput.setReceiver(memoryBuffer);

		uploadInput.addSucceededListener(event -> {
			byte[] bytes;

			try {
				bytes = memoryBuffer.getInputStream().readAllBytes();
			} catch (Exception e) {
				bytes = null;
			}

			disclaimer.setVisible(true);
			profilePicture = bytes;
			refreshProfilePicture();
		});

		uploadInput.getElement().addEventListener("file-remove", e -> {
			profilePicture = null;
			refreshProfilePicture();
			disclaimer.setVisible(false);
		});

		return uploadInput;
	}
}
