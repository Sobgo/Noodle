package com.example.application.views.courses;

import java.util.List;

import com.example.application.data.entity.Course;
import com.example.application.services.DbService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
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

@PageTitle("Noodle")
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class CoursesView extends Main implements HasComponents, HasStyle {
	
    private OrderedList imageContainer;
	private DbService db;

	public CoursesView(DbService db) {
		this.db = db;

		constructUI();

        List<Course> courses = this.db.gatAllCourses();
		for (Course course : courses) {
			imageContainer.add(new CoursesViewCard(course.getId(), course.getName(), course.getBannerUrl()));
		}

        if (courses.isEmpty()) {
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
        findBy.setPlaceholder("Find by name");
        findBy.setClearButtonVisible(true);
        findBy.setValueChangeMode(ValueChangeMode.LAZY);

        Select<String> sortBy = new Select<>();
        sortBy.setItems("Sort by name", "Sort by last access");
        sortBy.setValue("Sort by name");
        sortBy.setEmptySelectionAllowed(false);

        HorizontalLayout controls = new HorizontalLayout(findBy, sortBy);
        controls.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

        imageContainer = new OrderedList();
        imageContainer.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);

        container.add(headerContainer, controls);
        add(container, imageContainer);
    }
}
