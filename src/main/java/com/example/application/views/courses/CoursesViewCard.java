package com.example.application.views.courses;

import com.example.application.views.courseDetails.CourseDetailsView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

public class CoursesViewCard extends ListItem {;
    @Getter
    private Span name;

    public void updateTitle(String title) {
        name.setText(title);
    }

    public CoursesViewCard(Long id, String text, AbstractStreamResource imgSrc, boolean redirect) {
        setWidth("300px");

        addClassNames(
            Background.CONTRAST_5, Display.FLEX, FlexDirection.COLUMN, AlignItems.START,
            BorderRadius.LARGE
        );

        Div divIMG = new Div();
        divIMG.setHeight("140px");
        divIMG.addClassNames(
            Background.CONTRAST, Display.FLEX, AlignItems.CENTER, JustifyContent.CENTER,
            Overflow.HIDDEN, BorderRadius.MEDIUM, Width.FULL
        );

        Image image = new Image();
        image.setWidth("100%");

        image.setSrc(imgSrc);

        divIMG.add(image);

        VerticalLayout divTXT = new VerticalLayout();
        divTXT.addClassNames(Padding.Horizontal.SMALL, Padding.Bottom.SMALL);
        divTXT.setHeight("100px");

        name = new Span();
        name.addClassNames(FontSize.SMALL, TextColor.PRIMARY, Margin.Bottom.AUTO);
        name.setText(text);

        divTXT.add(name);

        add(divIMG, divTXT);

        getElement().getStyle().set("cursor", "pointer");

        if (redirect) {
            addClickListener(e -> 
                this.getUI().ifPresent(ui -> ui.navigate(CourseDetailsView.class, id))
            );
        }
    }
}
