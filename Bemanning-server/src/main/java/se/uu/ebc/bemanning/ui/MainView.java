package se.uu.ebc.bemanning.ui;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * Landing view served at the application root ({@code /}), rendered inside
 * {@link MainLayout}. Navigation and the budget-year selector are provided by
 * the layout's drawer, so this view is just a welcome page.
 */
@Route(value = "", layout = MainLayout.class)
@PageTitle("Bemanning")
@AnonymousAllowed // Vaadin navigation access control requires an explicit access
                  // annotation, otherwise the route is denied (403).
public class MainView extends VerticalLayout {

    public MainView() {
        setSizeFull();
        setSpacing(true);
        setPadding(true);

        add(new H1("Bemanningsplaneraren"));
        add(new Paragraph(
                "Use the menu on the left to choose a view, and the budget-year "
                        + "selector to scope the staff and assignment data by year."));
        add(new Anchor("legacy-redirect.html", "Legacy application (ExtJS)"));
    }
}
