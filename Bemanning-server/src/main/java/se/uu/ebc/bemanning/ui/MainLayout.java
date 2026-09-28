package se.uu.ebc.bemanning.ui;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.theme.lumo.LumoUtility;

import se.uu.ebc.bemanning.service.StaffService;

import lombok.extern.slf4j.Slf4j;

/**
 * Shared application layout ({@link AppLayout}) that hosts the application-wide
 * budget-year selector and the primary navigation.
 *
 * <p>The year selector lives in the left drawer. Changing it updates the
 * session-scoped {@link YearContext}, which year-scoped views subscribe to so
 * they reload their data for the selected year. The list of selectable years
 * comes from {@link StaffService#getStaffedYears()}; the current calendar year
 * (the {@code YearContext} default) is always included so the initial value is
 * selectable even if no data exists for it yet.
 *
 * <p>Views opt into this layout with {@code @Route(value = "...", layout =
 * MainLayout.class)}.
 */
@Slf4j
public class MainLayout extends AppLayout {

    private final transient YearContext yearContext;

    public MainLayout(YearContext yearContext, StaffService staffService) {
        this.yearContext = yearContext;

        // ---- Navbar (top bar): drawer toggle + app title ----
        DrawerToggle toggle = new DrawerToggle();
        H1 title = new H1("Bemanningsplaneraren");
        title.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.MEDIUM);
        addToNavbar(toggle, title);

        // ---- Drawer (left menu): year selector + navigation ----
        addToDrawer(buildDrawer(staffService));
    }

    private VerticalLayout buildDrawer(StaffService staffService) {
        ComboBox<String> yearPicker = new ComboBox<>("Budgetår");
        yearPicker.setItems(buildYearItems(staffService));
        yearPicker.setAllowCustomValue(false);
        yearPicker.setValue(yearContext.getYear());
        yearPicker.setWidthFull();
        // Ignore a null (cleared) selection so a year is always in effect.
        yearPicker.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                yearContext.setYear(e.getValue());
            }
        });

        // Navigation is driven by Vaadin's @Menu mechanism: every @Menu-annotated
        // route becomes a SideNav item automatically (in @Menu order), so adding a
        // new @Menu route shows up here without touching this layout.
        VerticalLayout drawer = new VerticalLayout(
                yearPicker,
                new H3("Views"),
                createSideNav());
        drawer.setPadding(true);
        drawer.setSpacing(true);
        return drawer;
    }

    private SideNav createSideNav() {
        var nav = new SideNav();
        nav.setWidthFull();
        MenuConfiguration.getMenuEntries().forEach(entry -> nav.addItem(createSideNavItem(entry)));
        return nav;
    }

    private SideNavItem createSideNavItem(MenuEntry menuEntry) {
        if (menuEntry.icon() != null) {
            Component icon = null;
            if (menuEntry.icon().contains(".svg")) {
                icon = new SvgIcon(menuEntry.icon());
            } else {
                icon = new Icon(menuEntry.icon());
            }
            return new SideNavItem(menuEntry.title(), menuEntry.menuClass(), icon);
        } else {
            return new SideNavItem(menuEntry.title(), menuEntry.menuClass());
        }
    }
 

    /**
     * Builds the selectable years: the distinct staffed years plus the current
     * default year (so the initial selection is always a valid item), preserving
     * descending order and avoiding duplicates.
     */
    private List<String> buildYearItems(StaffService staffService) {
        List<String> items = new ArrayList<>();
        String current = yearContext.getYear();
        if (current != null) {
            items.add(current);
        }
        try {
            for (String y : staffService.getStaffedYears()) {
                if (y != null && !items.contains(y)) {
                    items.add(y);
                }
            }
        } catch (Exception ex) {
            log.error("Failed to load staffed years", ex);
        }
        return items;
    }
}
