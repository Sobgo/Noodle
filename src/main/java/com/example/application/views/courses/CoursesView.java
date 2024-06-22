package com.example.application.views.courses;

import java.util.List;

import com.example.application.data.entity.CourseInfo;
import com.example.application.services.DbService;
import com.example.application.views.MainLayout;
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
public class CoursesView extends Main {
	
    private OrderedList imageContainer;
	private DbService db;

	public CoursesView(DbService db) {
		this.db = db;

		constructUI();

        List<CourseInfo> courses = this.db.getAllInfoOnly();
		for (CourseInfo info : courses) {
			imageContainer.add(new CoursesViewCard(info.getId(), info.getName(), info.getBanner()));
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
        findBy.addClassName("courses-view-text-field-1");
        
        findBy.setPlaceholder("Filter by name");
        findBy.setClearButtonVisible(true);
        findBy.setValueChangeMode(ValueChangeMode.LAZY);

        Select<String> sortBy = new Select<>();
        sortBy.setOverlayClassName("courses-view-select-1");
        sortBy.addClassName("courses-view-select-1");

        sortBy.setItems("Sort by name", "Sort by last access");
        sortBy.setValue("Sort by name");
        sortBy.setEmptySelectionAllowed(false);

        HorizontalLayout controls = new HorizontalLayout(findBy, sortBy);
        controls.setVerticalComponentAlignment(Alignment.STRETCH);

        imageContainer = new OrderedList();
        imageContainer.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);

        container.add(headerContainer, controls);
        add(container, imageContainer);
    }
}
