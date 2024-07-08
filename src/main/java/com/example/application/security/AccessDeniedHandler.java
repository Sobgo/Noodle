package com.example.application.security;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.*;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;

@Tag(Tag.DIV)
public class AccessDeniedHandler extends Component implements HasErrorParameter<AccessDeniedException> {

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<AccessDeniedException> parameter) {
        getElement().setText("Could not navigate to '" + event.getLocation().getPath()
                + "'. Reason: Access is denied by annotations on the view.");
        return HttpServletResponse.SC_FORBIDDEN;
    }
}
