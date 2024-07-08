package com.example.application.views.home;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.application.data.entity.Role;
import com.example.application.data.entity.User;
import com.example.application.data.entity.Course.CourseInfo;
import com.example.application.services.DbService;
import com.example.application.views.course.CourseDetailsView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.BorderRadius;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;

import lombok.Getter;

public class CourseCard extends ListItem {
    private DbService db;
    private UserDetailsService userDetailsService;

    @Getter
    private Span name;

    @Getter
    private Long courseId;

    public void updateTitle(String title) {
        name.setText(title);
    }

    public CourseCard(DbService db, UserDetailsService userDetailsService, Long id, String text,
            AbstractStreamResource imgSrc) {
        this(id, text, imgSrc);
        this.db = db;
        this.userDetailsService = userDetailsService;
        this.courseId = id;
    }

    // Non-interactive constructor for previewing
    public CourseCard(Long id, String text, AbstractStreamResource imgSrc) {
        constructUI(id, text, imgSrc);
    }

    public void constructUI(Long id, String text, AbstractStreamResource imgSrc) {
        setWidth("300px");
        getElement().getStyle().set("cursor", "pointer");

        addClassNames(
                Background.CONTRAST_5, Display.FLEX, FlexDirection.COLUMN, AlignItems.START,
                BorderRadius.LARGE);

        Image image = new Image();
        image.setWidth("100%");
        image.setSrc(imgSrc);

        Div divIMG = new Div();
        divIMG.setHeight("140px");
        divIMG.addClassNames(
                Background.CONTRAST, Display.FLEX, AlignItems.CENTER, JustifyContent.CENTER,
                Overflow.HIDDEN, BorderRadius.MEDIUM, Width.FULL);
        divIMG.add(image);

        name = new Span();
        name.addClassNames(FontSize.SMALL, TextColor.PRIMARY, Margin.Bottom.AUTO);
        name.setText(text);

        VerticalLayout divTXT = new VerticalLayout();
        divTXT.addClassNames(Padding.Horizontal.SMALL, Padding.Bottom.SMALL);
        divTXT.setHeight("100px");
        divTXT.add(name);

        add(divIMG, divTXT);
        setupOnClickAction(id);
    }

    private void setupOnClickAction(Long id) {
        addClickListener(e -> {
            if (db == null) return;

            CourseInfo course = db.getCourseInfo(id);
            if (course == null) return;

            if (hasAccess(course.getId())) {
                this.getUI().ifPresent(ui -> ui.navigate(CourseDetailsView.class, id));
                return;
            }

            Dialog dialog = new Dialog();
            VerticalLayout dialogLayout = new VerticalLayout();

            if (course.getKey() != null) {
                dialog.setHeaderTitle("Key Required");

                Span message = new Span("This course requires a key to access.");
                PasswordField key = new PasswordField("Enter Key:");

                Button submitButton = new Button("Submit", e2 -> {
                    if (isKeyValid(key.getValue(), id)) {
                        key.setInvalid(false);
                        dialog.close();
                        AllowAccess(course);
                    } else {
                        key.setInvalid(true);
                        key.setErrorMessage("Invalid key");
                    }
                });

                submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                submitButton.addClickShortcut(Key.ENTER);

                Button cancelButton = new Button("Cancel", e2 -> dialog.close());

                dialogLayout.add(message, key);
                dialog.getFooter().add(submitButton, cancelButton);
            } else {
                dialog.setHeaderTitle("Sign up for this course?");

                Button yesButton = new Button("Yes", e2 -> {
                    dialog.close();
                    AllowAccess(course);
                });

                yesButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                yesButton.addClickShortcut(Key.ENTER);

                Button cancelButton = new Button("Cancel", e2 -> dialog.close());

                dialogLayout.add(new Span("Would you like to sign up for this course?"));
                dialog.getFooter().add(yesButton, cancelButton);
            }

            dialogLayout.setPadding(false);
            dialogLayout.setSpacing(false);
            dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
            dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

            dialog.add(dialogLayout);
            dialog.open();
        });
    }

    private boolean isKeyValid(String key, Long id) {
        String courseKey = db.getCourse(id).getKey();
        if (courseKey == null) return false;
        return courseKey.equals(key);
    }

    public boolean hasAccess(Long courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String requiredRole = "ROLE_COURSE_" + courseId;

        boolean hasRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch((role) -> role.equals(requiredRole) || role.equals("ROLE_ADMIN"));

        return hasRole;
    }

    private void AllowAccess(CourseInfo course) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Role courseRole = course.getCourseRole();

        if (!hasAccess(course.getId())) {
            // grant role to user
            User user = db.getUserByUsername(authentication.getName());
            db.grantRole(user.getId(), courseRole.getId());

            // refresh session authorities
            UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    authentication.getCredentials(),
                    userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }

        this.getUI().ifPresent(ui -> ui.navigate(CourseDetailsView.class, course.getId()));
    }
}
