package se.uu.ebc.bemanning.ui;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * Landing view served at the application root ({@code /}).
 *
 * <p>The Vaadin servlet owns the root context (required for the React Router
 * bootstrap), so a route is needed for {@code ""} to avoid a "route not found"
 * page. This is a lightweight menu that links to the Vaadin views migrated so
 * far and to the legacy ExtJS SPA, which now lives at {@code /legacy-redirect.html}.
 */
@Route("")
@PageTitle("Bemanning")
@AnonymousAllowed // Mirrors CourseView: an explicit access annotation is required
                  // for Vaadin navigation, otherwise the route is denied (403).
public class MainView extends VerticalLayout {

    public MainView() {
        setSizeFull();
        setSpacing(true);
        setPadding(true);

        add(new H1("Bemanningsplaneraren"));
        add(new Paragraph("Welcome. Choose where to go:"));

        Button coursesButton = new Button("Open Courses",
                e -> getUI().ifPresent(ui -> ui.navigate(CourseView.class)));
        coursesButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button peopleButton = new Button("Open People",
                e -> getUI().ifPresent(ui -> ui.navigate(PersonView.class)));
        peopleButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        RouterLink coursesLink = new RouterLink("Courses", CourseView.class);
        RouterLink peopleLink = new RouterLink("People", PersonView.class);

        Anchor legacyLink = new Anchor("legacy-redirect.html", "Legacy application (ExtJS)");

        add(new HorizontalLayout(coursesButton, peopleButton), coursesLink, peopleLink, legacyLink);
    }
}
