package com.example.application.views.courses;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.application.data.entity.CourseClasses.CourseInfo;
import com.example.application.services.DbService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;

import jakarta.annotation.security.PermitAll;

@PageTitle("All Courses")
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class CoursesView extends Main {	
    private Map<CourseInfo, Optional<StreamResource>> courses = new LinkedHashMap<>();
    private OrderedList imageContainer;

    private DbService db;

    @Autowired
	private UserDetailsService userDetailsService;

    public CoursesView(DbService db) {
        this.db = db;

        constructUI();
    
        List<CourseInfo> coursesList = db.getAllInfoOnly();

        String path = "../../img/default.png";
        StreamResource defBanner = new StreamResource("default.jpg", () -> getClass().getResourceAsStream(path));

        for (CourseInfo info : coursesList) {
            final byte[] bytes = info.getBanner();

            StreamResource banner;

            if (bytes == null) {
                banner = defBanner;
            } else {
                banner = new StreamResource("banner", () -> new ByteArrayInputStream(bytes));
            }

            courses.put(info, Optional.of(banner));
        }
    }

    public void onAttach(AttachEvent attachEvent) {
        filterList("", "Sort by name");
    }

    private void filterList(String key, String sortBy) {
        imageContainer.removeAll();

        List<CoursesViewCard> temp = new ArrayList<>();

        for (Map.Entry<CourseInfo, Optional<StreamResource>> entry : courses.entrySet()) {
            if (entry.getKey().getName().toLowerCase().contains(key)) {
                CourseInfo info = entry.getKey();
                StreamResource banner = entry.getValue().get();

                CoursesViewCard card = new CoursesViewCard(db, userDetailsService, info.getId(), info.getName(), banner);
                temp.add(card);
            }
        }

        if (sortBy.equals("Sort by name")) {
            temp.sort((CoursesViewCard a, CoursesViewCard b) -> a.getName().getText().compareTo(b.getName().getText()));
            imageContainer.add(temp.toArray(new CoursesViewCard[temp.size()]));
        } else if (sortBy.equals("Sort by my courses first")) {
            List<CoursesViewCard> myCourses = new ArrayList<>();
            List<CoursesViewCard> otherCourses = new ArrayList<>();

            for (CoursesViewCard card : temp) {
                if (card.hasAccess(card.getCourseId())) {
                    myCourses.add(card);
                } else {
                    otherCourses.add(card);
                }
            }

            myCourses.sort((CoursesViewCard a, CoursesViewCard b) -> a.getName().getText().compareTo(b.getName().getText()));
            otherCourses.sort((CoursesViewCard a, CoursesViewCard b) -> a.getName().getText().compareTo(b.getName().getText()));

            myCourses.addAll(otherCourses);
            imageContainer.add(myCourses.toArray(new CoursesViewCard[myCourses.size()]));
        }

        if (imageContainer.getChildren().count() == 0) {
            imageContainer.add("No courses found");
        }
    }

    private void constructUI() {
        addClassNames("courses-view");
        addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(AlignItems.CENTER, JustifyContent.BETWEEN);

        VerticalLayout headerContainer = new VerticalLayout();
        
        H2 header = new H2("All Courses");
        header.addClassNames(Margin.Bottom.NONE, Margin.Top.XLARGE, FontSize.XXXLARGE);

        headerContainer.add(header);

        TextField findBy = new TextField();
        findBy.addClassName("courses-view-text-field-1");
        
        findBy.setPlaceholder("Filter by name");
        findBy.setClearButtonVisible(true);
        findBy.setValueChangeMode(ValueChangeMode.EAGER);

        Select<String> sortBy = new Select<>();
        sortBy.setOverlayClassName("courses-view-select-1");
        sortBy.addClassName("courses-view-select-1");

        sortBy.setItems("Sort by name", "Sort by my courses first");
        sortBy.setValue("Sort by name");
        sortBy.setEmptySelectionAllowed(false);

        findBy.addValueChangeListener(e -> filterList(e.getValue().toLowerCase(), sortBy.getValue()));
        sortBy.addValueChangeListener(e -> filterList(findBy.getValue().toLowerCase(), e.getValue()));

        HorizontalLayout controls = new HorizontalLayout(findBy, sortBy);
        controls.setVerticalComponentAlignment(Alignment.STRETCH);

        imageContainer = new OrderedList();
        imageContainer.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);

        container.add(headerContainer, controls);
        add(container, imageContainer);
    }
}
