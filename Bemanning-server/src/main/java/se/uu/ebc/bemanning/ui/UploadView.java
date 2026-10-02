package se.uu.ebc.bemanning.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import lombok.extern.slf4j.Slf4j;

/**
 * Single sidebar entry ("Import") that hosts the two file-import views in a tab
 * bar. Both upload views are embedded directly as components (not separate
 * routes), so switching tabs never navigates away from this route - the shared
 * {@link MainLayout} drawer/sidebar stays visible the whole time.
 *
 * <p>The embedded views ({@link TEUploadView} and {@link PrimulaUploadView}) are
 * prototype-scoped Spring beans, injected here so their {@code @Value} config
 * and {@code @PreAuthorize} security remain active.
 */
@Route(value = "imports", layout = MainLayout.class)
@PageTitle("Import")
@Menu(order = 7, icon = "icons/upload.svg", title = "Filimport")
@AnonymousAllowed // The embedded views keep their own @PreAuthorize-guarded actions.
@Slf4j
public class UploadView extends VerticalLayout {

    private final Tabs tabs = new Tabs();
    private final Tab teTab = new Tab("TimeEdit-import");
    private final Tab primulaTab = new Tab("Primula-import");

    // The two import views, embedded as components (one is visible at a time).
    private final Component teView;
    private final Component primulaView;

    // Where the active tab's view is rendered (below the tab bar).
    private final VerticalLayout content = new VerticalLayout();

    public UploadView(TEUploadView teView, PrimulaUploadView primulaView) {
        this.teView = teView;
        this.primulaView = primulaView;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        tabs.add(teTab, primulaTab);
        tabs.setWidthFull();
        tabs.addSelectedChangeListener(e -> showSelectedTab());

        content.setSizeFull();
        content.setPadding(false);

        add(tabs, content);

        // Start on the first tab (TimeEdit).
        tabs.setSelectedTab(teTab);
        showSelectedTab();
    }

    /** Swaps the content area to the view for the currently selected tab. */
    private void showSelectedTab() {
        content.removeAll();
        content.add(tabs.getSelectedTab() == primulaTab ? primulaView : teView);
    }
}
