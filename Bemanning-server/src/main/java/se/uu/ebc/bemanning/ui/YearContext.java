package se.uu.ebc.bemanning.ui;

import java.io.Serializable;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;

import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;

/**
 * Application-wide (per Vaadin session) holder for the currently selected
 * budget year.
 *
 * <p>The year selector in {@link MainLayout} writes the chosen year here, and
 * year-scoped views ({@code StaffView}, {@code AssignmentView}) read it and
 * subscribe to changes so they can reload their data for the selected year.
 *
 * <p>Scope is {@link VaadinSessionScope}: one instance per user session (shared
 * across that user's browser tabs, isolated between users). It is injectable
 * into layouts and views via the constructor.
 */
@Component
@VaadinSessionScope
public class YearContext implements Serializable {

    /** Currently selected year, e.g. "2026". Defaults to the current calendar year. */
    private String year = String.valueOf(Year.now().getValue());

    private final List<Consumer<String>> listeners = new ArrayList<>();

    public String getYear() {
        return year;
    }

    /**
     * Sets the selected year and notifies all registered listeners. A no-op if
     * the value is unchanged, to avoid redundant reloads.
     */
    public void setYear(String year) {
        if (java.util.Objects.equals(this.year, year)) {
            return;
        }
        this.year = year;
        // Copy first so a listener may unregister itself during notification.
        new ArrayList<>(listeners).forEach(listener -> listener.accept(year));
    }

    /**
     * Registers a listener invoked whenever the selected year changes. The
     * returned {@link Registration} removes the listener; callers should remove
     * it when their component detaches to avoid leaks.
     */
    public Registration addYearChangeListener(Consumer<String> listener) {
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }
}
