package com.example.application.views.login;

import com.example.application.security.AuthenticatedUser;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("Login to Noodle")
@Route(value = "login")
public class LoginView extends LoginOverlay implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;

    public LoginView(AuthenticatedUser authenticatedUser) {
        addClassName("login-view-login-overlay");

        this.authenticatedUser = authenticatedUser;
        setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Noodle");
        i18n.getHeader().setDescription("Course management system");
        i18n.setAdditionalInformation(null);
       
        i18n.getForm().setForgotPassword("Create new account");
        setForgotPasswordButtonVisible(true);

        addForgotPasswordListener(e -> {
            setOpened(false);
            UI.getCurrent().navigate("signup");
        });

        setI18n(i18n);
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
