package com.example.application.views.login;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.application.security.AuthenticatedUser;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("Sign Up to Noodle")
@Route(value = "signup")
public class SignUpView extends LoginOverlay implements BeforeEnterObserver {

	private final AuthenticatedUser authenticatedUser;
	private LoginI18n i18n = LoginI18n.createDefault();

	public void validateNewUser(String username, String password, String repeatPassword) {
		if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
			setError(true);
			i18n.getErrorMessage().setTitle("Username and password are required");
			i18n.getErrorMessage().setMessage("Please try again.");
			setI18n(i18n);
			return;
		}

		if (!password.equals(repeatPassword)) {
			setError(true);
			i18n.getErrorMessage().setTitle("Passwords do not match");
			i18n.getErrorMessage().setMessage("Please try again.");
			setI18n(i18n);
			return;
		}

		setError(false);
	}

	public SignUpView(AuthenticatedUser authenticatedUser, BCryptPasswordEncoder passwordEncoder) {
		addClassName("login-view-login-overlay");

		this.authenticatedUser = authenticatedUser;
		setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));

		i18n.setHeader(new LoginI18n.Header());
		i18n.getHeader().setTitle("Noodle");
		i18n.getHeader().setDescription("Course management system");

		i18n.getForm().setTitle("Sign Up to Noodle");
		i18n.getForm().setSubmit("Create account");

		PasswordField repeatPasswordField = new PasswordField();
		repeatPasswordField.setLabel("Repeat password");
		repeatPasswordField.setRequired(true);
		repeatPasswordField.setErrorMessage("Repeat password is required");

		getCustomFormArea().add(repeatPasswordField);

		addLoginListener(e -> {
			setEnabled(false);

			validateNewUser(e.getUsername(), e.getPassword(), repeatPasswordField.getValue());
			if (isError()) {
				return;
			}

			if (!authenticatedUser.register(e.getUsername(), passwordEncoder.encode(e.getPassword()))) {
				setError(true);
				i18n.getErrorMessage().setTitle("Username already exists");
				i18n.getErrorMessage().setMessage("Please try different username.");
				setI18n(i18n);
				return;
			}

			setEnabled(true);
			UI.getCurrent().navigate("login?created");
		});

		i18n.getForm().setForgotPassword("Back to login");

		addForgotPasswordListener(e -> {
			setOpened(false);
			UI.getCurrent().navigate("login");
		});

		setI18n(i18n);

		setAction("");
		setForgotPasswordButtonVisible(true);
		setOpened(true);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		// Already logged in
		if (authenticatedUser.get().isPresent()) {
			setOpened(false);
			event.forwardTo("home");
		}
	}
}
