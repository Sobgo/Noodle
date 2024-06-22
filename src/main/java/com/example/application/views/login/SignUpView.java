package com.example.application.views.login;

import com.example.application.security.AuthenticatedUser;
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

	public SignUpView(AuthenticatedUser authenticatedUser) {
		addClassName("login-view-login-overlay");

		this.authenticatedUser = authenticatedUser;
		setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));
		
		LoginI18n i18n = LoginI18n.createDefault();
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
		
		setI18n(i18n);

		addLoginListener(e -> {
			setOpened(false);
			// TODO: Implement user registration
		});

		setForgotPasswordButtonVisible(false);
		setOpened(true);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		// Already logged in
		if (authenticatedUser.get().isPresent()) {
			setOpened(false);
			event.forwardTo("home");
		}

		setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
	}

}
