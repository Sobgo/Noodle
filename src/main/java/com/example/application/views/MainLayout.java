package com.example.application.views;

import com.example.application.data.entity.User;
import com.example.application.data.entity.Course.CourseInfo;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.DbService;
import com.example.application.services.GlobalAccessService;
import com.example.application.views.controls.AdminView;
import com.example.application.views.home.CoursesView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {
    private H1 viewTitle;

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;
    private DbService db;

    private Scroller scroller;
    private Footer footer;
    private Avatar avatar;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker, DbService dbService,
            GlobalAccessService globalAccessService) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;
        this.db = dbService;
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();

        globalAccessService.setMainLayout(this);
    }

    public void refreshProfilePicture() {
        Optional<User> maybeUser = authenticatedUser.get();

        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            byte[] profilePicture = user.getProfilePicture();

            if (profilePicture != null) {
                StreamResource resource = new StreamResource("profile-pic",
                        () -> new ByteArrayInputStream(profilePicture));
                avatar.setImageResource(resource);
            } else {
                avatar.setImage(null);
            }
        }
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        Span appName = new Span("Noodle");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        scroller = new Scroller(createNavigation());
        footer = createFooter();

        addToDrawer(header, scroller, footer);
    }

    private VerticalLayout createNavigation() {
        VerticalLayout layout = new VerticalLayout();

        SideNav nav = new SideNav();

        if (accessChecker.hasAccess(CoursesView.class)) {
            SideNavItem item1 = new SideNavItem("Home", CoursesView.class, LineAwesomeIcon.HOME_SOLID.create());
            item1.addClassNames(LumoUtility.Margin.Bottom.SMALL);
            nav.addItem(item1);
        }

        if (accessChecker.hasAccess(AdminView.class)) {
            SideNavItem item2 = new SideNavItem("Admin", AdminView.class,
                    LineAwesomeIcon.TACHOMETER_ALT_SOLID.create());
            item2.addClassNames(LumoUtility.Margin.Bottom.SMALL);
            nav.addItem(item2);
        }

        SideNav courses = new SideNav();
        courses.setLabel("My Courses");
        courses.setCollapsible(true);

        List<CourseInfo> courseInfos = db.getUserRegisteredCourses(authenticatedUser.get().get().getId());

        for (CourseInfo courseInfo : courseInfos) {
            String navigationTarget = "details/" + courseInfo.getId();
            SideNavItem item = new SideNavItem(courseInfo.getName(), navigationTarget,
                    LineAwesomeIcon.BOOK_SOLID.create());
            item.addClassNames(LumoUtility.Margin.Bottom.SMALL);
            courses.addItem(item);
        }

        layout.setSpacing(true);
        layout.setSizeUndefined();
        nav.setWidthFull();
        courses.setWidthFull();

        UI.getCurrent().addAfterNavigationListener(event -> {
            viewTitle.setText(getCurrentPageTitle());
        });

        layout.add(nav, courses);
        return layout;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<User> maybeUser = authenticatedUser.get();

        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            avatar = new Avatar(user.getUsername());
            refreshProfilePicture();
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getUsername());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);

            userName.getSubMenu().addItem("Account settings", e -> {
                UI.getCurrent().navigate("account");
            });

            userName.getSubMenu().addItem("Sign out", e -> {
                authenticatedUser.logout();
            });

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());

        remove(scroller);
        remove(footer);
        scroller = new Scroller(createNavigation());
        footer = createFooter();
        addToDrawer(scroller, footer);
    }

    private String getCurrentPageTitle() {
        if (getContent() == null) {
            return "Noodle";
        }

        PageTitle PageTitle = getContent().getClass().getAnnotation(PageTitle.class);
        String title = null;

        if (PageTitle == null) {
            title = UI.getCurrent().getInternals().getTitle();
        } else {
            title = PageTitle.value();
        }

        return title != null ? title : "Noodle";
    }
}
