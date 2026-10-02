package se.uu.ebc.bemanning.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
//import java.time.format.DateTimeFormatter;
//import java.time.LocalDateTime;

import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import se.uu.ebc.bemanning.entity.PhDPosition;
import se.uu.ebc.bemanning.entity.Person;
import se.uu.ebc.bemanning.entity.Progress;
import se.uu.ebc.bemanning.service.PeopleService;
import se.uu.ebc.bemanning.service.PhDService;

import lombok.extern.slf4j.Slf4j;

/**
 * Server-side Vaadin view for editing {@link PhDPosition} entities with a
 * buffered inline grid editor, plus a nested inline-editable grid for each
 * position's {@link Progress} entries.
 *
 * <p>The master grid lists PhD positions; expanding a row (item-details) reveals
 * a nested grid of that position's progress records, itself inline-editable.
 * Progress rows are persisted directly via {@link PhDService#saveProgress(Progress)}.
 *
 * <p>Data access goes through {@link PhDService}; the person selector is backed
 * by {@link PeopleService}.
 */
@Route(value = "phdpositions", layout = MainLayout.class)
@PageTitle("PhD positions")
@Menu(order = 6, icon = "icons/academy-cap.svg", title = "Doktorander")
@AnonymousAllowed // Vaadin navigation access control requires an explicit access
                  // annotation. Write actions remain guarded by @PreAuthorize.
@Slf4j
public class PhDPositionView extends VerticalLayout {

    private final PhDService phdService;
    private final PeopleService peopleService;
    private final YearContext yearContext;

    private final Grid<PhDPosition> grid = new Grid<>(PhDPosition.class, false);
    private final Binder<PhDPosition> binder = new Binder<>(PhDPosition.class);
    private final Editor<PhDPosition> editor = grid.getEditor();

    private final List<PhDPosition> positions = new ArrayList<>();
    // In-memory list data view captured from grid.setItems(...); used to apply
    // the Person / Program column filters.
    private com.vaadin.flow.component.grid.dataview.GridListDataView<PhDPosition> dataView;

    // Holds the current Person / Program filter criteria (AND-ed).
    private final PhDFilter filter = new PhDFilter();

    private final Button newButton = new Button("Ny doktorand");

    private PhDPosition pendingNew;

    public PhDPositionView(PhDService phdService, PeopleService peopleService,
                           YearContext yearContext) {
        this.phdService = phdService;
        this.peopleService = peopleService;
        this.yearContext = yearContext;
        setSizeFull();

        configureGrid();
        configureEditor();
        add(buildToolbar(), grid);
        loadPositions();
    }

    private HorizontalLayout buildToolbar() {
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newButton.addClickListener(e -> addPosition());

        // Combined active-status filter. A position is "not active" if it has a
        // dissertation date OR its inactive flag is set (see isActive()).
        ComboBox<String> statusSelect = new ComboBox<>("Status");
        statusSelect.setItems("Aktiv", "Inaktiv");
        statusSelect.setPlaceholder("Alla");
        statusSelect.setClearButtonVisible(true);
        statusSelect.addValueChangeListener(e -> {
            String v = e.getValue();
            // null (cleared) -> any; "Aktiv" -> active only; "Inaktiv" -> not active.
            filter.active = v == null ? null : "Aktiv".equals(v);
            applyFilter();
        });

        HorizontalLayout toolbar = new HorizontalLayout(newButton, statusSelect);
        toolbar.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.END);
        return toolbar;
    }

    /* ---- Master grid (PhDPosition) ---- */

    private void configureGrid() {
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();

        Grid.Column<PhDPosition> personCol = grid.addColumn(this::personLabel)
                .setHeader("Person").setSortable(true).setAutoWidth(true);
        // Computed: the person's affiliation for the current budget year.
        // Sortable by the computed label (case-insensitive).
        Grid.Column<PhDPosition> programCol = grid.addColumn(this::programLabel)
                .setHeader("Program").setAutoWidth(true)
                .setSortable(true)
                .setComparator(java.util.Comparator.comparing(
                        this::programLabel, String.CASE_INSENSITIVE_ORDER));
        Grid.Column<PhDPosition> startCol = grid.addColumn(p -> dateLabel(p.getStart()))
                .setHeader("Start").setSortable(true).setWidth("100px").setFlexGrow(0);
        // Sort by the underlying date (nulls last) rather than the display string.
        Grid.Column<PhDPosition> dissertationCol = grid.addColumn(p -> dateLabel(p.getDissertation()))
                .setHeader("Disputation").setWidth("100px").setFlexGrow(0)
                .setSortable(true)
                .setComparator(java.util.Comparator.comparing(
                        PhDPosition::getDissertation,
                        java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())));
        Grid.Column<PhDPosition> inactiveCol = grid.addColumn(p -> p.isInactive() ? "Ja" : "Nej")
                .setHeader("Inaktiv").setWidth("80px").setFlexGrow(0);
        Grid.Column<PhDPosition> noteCol = grid.addColumn(PhDPosition::getNote)
                .setHeader("Anteckningar").setAutoWidth(true);

        Grid.Column<PhDPosition> actionsCol = grid.addComponentColumn(this::buildRowActions)
                .setHeader("Handling").setAutoWidth(true).setFlexGrow(0);

        buildEditorComponents(personCol, startCol, dissertationCol, inactiveCol, noteCol, actionsCol);

        buildFilterRow(personCol, programCol);

        // Nested grid: each master row expands to its Progress records.
        grid.setItemDetailsRenderer(new ComponentRenderer<>(this::buildProgressPanel));
    }

    /**
     * Adds a header filter row with Person and Program text filters (AND-ed).
     * The combined active-status filter lives in the toolbar (see buildToolbar).
     */
    private void buildFilterRow(Grid.Column<PhDPosition> personCol,
                                Grid.Column<PhDPosition> programCol) {
        com.vaadin.flow.component.grid.HeaderRow filterRow = grid.appendHeaderRow();
        filterRow.getCell(personCol).setComponent(
                textFilter("Person", value -> filter.person = value));
        filterRow.getCell(programCol).setComponent(
                textFilter("Program", value -> filter.program = value));
    }

    /**
     * A position is <em>not active</em> when it has a dissertation date OR its
     * inactive flag is set; otherwise it is active.
     */
    private boolean isActive(PhDPosition p) {
        boolean notActive = p.getDissertation() != null || p.isInactive();
        return !notActive;
    }

    private TextField textFilter(String placeholder, java.util.function.Consumer<String> setter) {
        TextField field = new TextField();
        field.setPlaceholder(placeholder);
        field.setClearButtonVisible(true);
        field.setWidthFull();
        field.setValueChangeMode(com.vaadin.flow.data.value.ValueChangeMode.LAZY);
        field.addValueChangeListener(e -> {
            setter.accept(e.getValue());
            applyFilter();
        });
        return field;
    }

    private void applyFilter() {
        if (dataView != null) {
            dataView.setFilter(filter::test);
        }
    }

    private void buildEditorComponents(Grid.Column<PhDPosition> personCol,
                                       Grid.Column<PhDPosition> startCol,
                                       Grid.Column<PhDPosition> dissertationCol,
                                       Grid.Column<PhDPosition> inactiveCol,
                                       Grid.Column<PhDPosition> noteCol,
                                       Grid.Column<PhDPosition> actionsCol) {
        ComboBox<Person> personField = new ComboBox<>();
        personField.setItems(safeList(peopleService::getAllPersons));
        personField.setItemLabelGenerator(p -> p == null ? "" : p.getName());
        personField.setWidthFull();
        binder.forField(personField)
                .asRequired("Person is required")
                .bind(PhDPosition::getPerson, PhDPosition::setPerson);
        personCol.setEditorComponent(personField);

        DatePicker startField = new DatePicker();
        startField.setWidthFull();
        binder.forField(startField)
                .asRequired("Start is required")
                .withConverter(this::toDateTime, this::toLocalDate)
                .bind(PhDPosition::getStart, PhDPosition::setStart);
        startCol.setEditorComponent(startField);

        DatePicker dissertationField = new DatePicker();
        dissertationField.setWidthFull();
        binder.forField(dissertationField)
                .withConverter(this::toDateTime, this::toLocalDate)
                .bind(PhDPosition::getDissertation, PhDPosition::setDissertation);
        dissertationCol.setEditorComponent(dissertationField);

        Checkbox inactiveField = new Checkbox();
        binder.forField(inactiveField)
                .bind(PhDPosition::isInactive, PhDPosition::setInactive);
        inactiveCol.setEditorComponent(inactiveField);

        TextField noteField = new TextField();
        noteField.setWidthFull();
        binder.forField(noteField)
                .bind(PhDPosition::getNote, PhDPosition::setNote);
        noteCol.setEditorComponent(noteField);

        actionsCol.setEditorComponent(buildEditorActions(this::save));
    }

    private void configureEditor() {
        editor.setBinder(binder);
        editor.setBuffered(true);
        editor.addCancelListener(e -> discardPendingNew());
    }

    private HorizontalLayout buildRowActions(PhDPosition position) {
        Button edit = new Button("Edit", e -> editItem(position));

        Button delete = new Button("Delete", e -> delete(position));
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        delete.setEnabled(position.getId() != null);

        // Toggle the nested Progress grid for this position.
        Button toggle = new Button("Progression", e -> grid.setDetailsVisible(position,
                !grid.isDetailsVisible(position)));
        toggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout actions = new HorizontalLayout(edit, delete, toggle);
        actions.setPadding(false);
        return actions;
    }

    private HorizontalLayout buildEditorActions(Runnable saveAction) {
        Button save = new Button("Save", e -> saveAction.run());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);

        Button cancel = new Button("Cancel", e -> editor.cancel());

        HorizontalLayout actions = new HorizontalLayout(save, cancel);
        actions.setPadding(false);
        return actions;
    }

    /* ---- Nested grid (Progress) ---- */

    /**
     * Builds the details panel shown when a position row is expanded: a heading,
     * a toolbar to add a progress row, and an inline-editable grid of the
     * position's {@link Progress} entries.
     */
    private Component buildProgressPanel(PhDPosition position) {
        Grid<Progress> progressGrid = new Grid<>(Progress.class, false);
        Binder<Progress> progressBinder = new Binder<>(Progress.class);
        Editor<Progress> progressEditor = progressGrid.getEditor();
        progressEditor.setBinder(progressBinder);
        progressEditor.setBuffered(true);

        progressGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        progressGrid.setAllRowsVisible(true);

        Grid.Column<Progress> dateCol = progressGrid.addColumn(p -> dateLabel(p.getDate()))
                .setHeader("Datum").setAutoWidth(true);
        Grid.Column<Progress> activityCol = progressGrid.addColumn(Progress::getActivity)
                .setHeader("Aktivitetsgrad").setAutoWidth(true);
        Grid.Column<Progress> projectCol = progressGrid.addColumn(Progress::getProjectFraction)
                .setHeader("Andel projekt").setAutoWidth(true);
        Grid.Column<Progress> guCol = progressGrid.addColumn(Progress::getGuFraction)
                .setHeader("Andel UGA").setAutoWidth(true);
        Grid.Column<Progress> remainingCol = progressGrid.addColumn(Progress::getRemainingMonths)
                .setHeader("Återstående månader").setAutoWidth(true);
        Grid.Column<Progress> addedCol = progressGrid.addColumn(Progress::getAddedMonths)
                .setHeader("Tillagda månader").setAutoWidth(true);
        Grid.Column<Progress> ecoCol = progressGrid.addColumn(p -> p.isToEcoSys() ? "Yes" : "No")
                .setHeader("Till Primula").setAutoWidth(true);
        Grid.Column<Progress> updokCol = progressGrid.addColumn(p -> p.isToUpDok() ? "Yes" : "No")
                .setHeader("Till LADOK").setAutoWidth(true);
        Grid.Column<Progress> noteCol = progressGrid.addColumn(Progress::getNote)
                .setHeader("Anteckninagr").setAutoWidth(true);
        Grid.Column<Progress> actionsCol = progressGrid.addComponentColumn(
                p -> buildProgressRowActions(position, progressGrid, progressEditor, p))
                .setHeader("Handling").setAutoWidth(true).setFlexGrow(0);

        // Editors for the progress columns.
        DatePicker dateField = new DatePicker();
        progressBinder.forField(dateField)
                .asRequired("Date is required")
                .withConverter(this::toDateTime, this::toLocalDate)
                .bind(Progress::getDate, Progress::setDate);
        dateCol.setEditorComponent(dateField);

        activityCol.setEditorComponent(floatField(progressBinder, Progress::getActivity, Progress::setActivity));
        projectCol.setEditorComponent(floatField(progressBinder, Progress::getProjectFraction, Progress::setProjectFraction));
        guCol.setEditorComponent(floatField(progressBinder, Progress::getGuFraction, Progress::setGuFraction));
        remainingCol.setEditorComponent(floatField(progressBinder, Progress::getRemainingMonths, Progress::setRemainingMonths));
        addedCol.setEditorComponent(floatField(progressBinder, Progress::getAddedMonths, Progress::setAddedMonths));

        Checkbox ecoField = new Checkbox();
        progressBinder.forField(ecoField).bind(Progress::isToEcoSys, Progress::setToEcoSys);
        ecoCol.setEditorComponent(ecoField);

        Checkbox updokField = new Checkbox();
        progressBinder.forField(updokField).bind(Progress::isToUpDok, Progress::setToUpDok);
        updokCol.setEditorComponent(updokField);

        TextField progressNote = new TextField();
        progressNote.setWidthFull();
        progressBinder.forField(progressNote).bind(Progress::getNote, Progress::setNote);
        noteCol.setEditorComponent(progressNote);

        actionsCol.setEditorComponent(buildProgressEditorActions(position, progressGrid, progressEditor));

        progressGrid.setItems(new ArrayList<>(position.getProgresses()));

        Button addProgress = new Button("Ny progression", e -> {
            if (progressEditor.isOpen()) {
                progressEditor.cancel();
            }
            Progress fresh = new Progress();
            fresh.setPhdPosition(position);
            fresh.setDate(LocalDateTime.now());
            List<Progress> rows = new ArrayList<>(position.getProgresses());
            rows.add(0, fresh);
            progressGrid.setItems(rows);
            progressEditor.editItem(fresh);
        });
        addProgress.addThemeVariants(ButtonVariant.LUMO_SMALL);

        VerticalLayout panel = new VerticalLayout(
                new H4("Progress"),
                new HorizontalLayout(addProgress),
                progressGrid);
        panel.setPadding(true);
        panel.setSpacing(true);
        return panel;
    }

    private HorizontalLayout buildProgressRowActions(PhDPosition position,
                                                     Grid<Progress> progressGrid,
                                                     Editor<Progress> progressEditor,
                                                     Progress progress) {
        Button edit = new Button("Edit", e -> {
            if (!progressEditor.isOpen()) {
                progressEditor.editItem(progress);
            }
        });
        Button delete = new Button("Delete",
                e -> deleteProgress(position, progressGrid, progressEditor, progress));
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout actions = new HorizontalLayout(edit, delete);
        actions.setPadding(false);
        return actions;
    }

    private HorizontalLayout buildProgressEditorActions(PhDPosition position,
                                                        Grid<Progress> progressGrid,
                                                        Editor<Progress> progressEditor) {
        Button save = new Button("Save", e -> saveProgress(position, progressGrid, progressEditor));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        save.addClickShortcut(Key.ENTER);

        Button cancel = new Button("Cancel", e -> progressEditor.cancel());
        cancel.addThemeVariants(ButtonVariant.LUMO_SMALL);

        HorizontalLayout actions = new HorizontalLayout(save, cancel);
        actions.setPadding(false);
        return actions;
    }

    /* ---- Master data / actions ---- */

    private void editItem(PhDPosition position) {
        if (editor.isOpen()) {
            editor.cancel();
        }
        editor.editItem(position);
    }

    private void addPosition() {
        if (editor.isOpen()) {
            editor.cancel();
        }
        PhDPosition fresh = new PhDPosition();
        positions.add(0, fresh);
        grid.getListDataView().refreshAll();
        pendingNew = fresh;
        editor.editItem(fresh);
    }

    private void loadPositions() {
        try {
            positions.clear();
            positions.addAll(phdService.getAllPhDPositions());
            dataView = grid.setItems(positions);
            applyFilter();
        } catch (Exception ex) {
            log.error("Failed to load PhD positions", ex);
            notifyError("Could not load PhD positions: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_PHDADMIN')")
    private void save() {
        PhDPosition edited = editor.getItem();
        if (edited == null) {
            return;
        }
        if (!editor.save()) {
            notifyError("Please fix the highlighted fields.");
            return;
        }
        try {
            phdService.savePhDPosition(edited);
            pendingNew = null;
            notifySuccess("PhD position saved.");
            loadPositions();
        } catch (Exception ex) {
            log.error("Failed to save PhD position", ex);
            notifyError("Could not save PhD position: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_PHDADMIN')")
    private void delete(PhDPosition position) {
        if (position == null) {
            return;
        }
        try {
            phdService.deletePhDPosition(position);
            notifySuccess("PhD position deleted.");
            loadPositions();
        } catch (Exception ex) {
            log.error("Failed to delete PhD position", ex);
            notifyError("Could not delete PhD position: " + ex.getMessage());
        }
    }

    private void discardPendingNew() {
        if (pendingNew != null) {
            positions.remove(pendingNew);
            pendingNew = null;
            grid.getListDataView().refreshAll();
        }
    }

    /* ---- Progress data / actions ---- */

    /**
     * Commits the progress editor and persists the {@link Progress} entity
     * directly via {@link PhDService#saveProgress(Progress)}. The back-reference
     * to the owning position is set first (it is required / the FK owner).
     */
    @PreAuthorize("hasRole('ROLE_PHDADMIN')")
    private void saveProgress(PhDPosition position, Grid<Progress> progressGrid,
                              Editor<Progress> progressEditor) {
        Progress edited = progressEditor.getItem();
        if (edited == null) {
            return;
        }
        if (!progressEditor.save()) {
            notifyError("Please fix the highlighted fields.");
            return;
        }
        edited.setPhdPosition(position);
        try {
            phdService.saveProgress(edited);
            notifySuccess("Progress saved.");
            reloadProgress(position, progressGrid);
        } catch (Exception ex) {
            log.error("Failed to save progress", ex);
            notifyError("Could not save progress: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_PHDADMIN')")
    private void deleteProgress(PhDPosition position, Grid<Progress> progressGrid,
                                Editor<Progress> progressEditor, Progress progress) {
        if (progress == null) {
            return;
        }
        if (progressEditor.isOpen()) {
            progressEditor.cancel();
        }
        try {
            if (progress.getId() != null) {
                phdService.deleteProgress(progress.getId());
            } else {
                // Never-persisted new row: just drop it from the grid.
                position.getProgresses().remove(progress);
            }
            notifySuccess("Progress deleted.");
            reloadProgress(position, progressGrid);
        } catch (Exception ex) {
            log.error("Failed to delete progress", ex);
            notifyError("Could not delete progress: " + ex.getMessage());
        }
    }

    /** Refreshes the nested grid from the database for the given position. */
    private void reloadProgress(PhDPosition position, Grid<Progress> progressGrid) {
        if (position == null || progressGrid == null) {
            return;
        }
        try {
            PhDPosition fresh = phdService.getPhDById(position.getId());
            progressGrid.setItems(new ArrayList<>(fresh.getProgresses()));
        } catch (Exception ex) {
            progressGrid.setItems(new ArrayList<>(position.getProgresses()));
        }
    }

    /* ---- Helpers ---- */

    private NumberField floatField(Binder<Progress> binder,
                                   com.vaadin.flow.function.ValueProvider<Progress, Float> getter,
                                   com.vaadin.flow.data.binder.Setter<Progress, Float> setter) {
        NumberField field = new NumberField();
        field.setStep(0.1);
        field.setWidthFull();
        binder.forField(field).withConverter(toFloat(), toDouble()).bind(getter, setter);
        return field;
    }

    private String personLabel(PhDPosition p) {
        return p.getPerson() == null ? "" : p.getPerson().getName();
    }

    /**
     * Computed Program column: the person's current affiliation for the selected
     * budget year, resolved via {@link PhDService#findCurrentAffiliation}.
     */
    private String programLabel(PhDPosition p) {

    	return p.getOuProxy();
/*
        if (p.getPerson() == null) {
            return "";
        }
		String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        if (year == null || year.isBlank()) {
            return null;
        }
        try {
            return phdService.findCurrentAffiliation(p.getPerson(), year);
        } catch (Exception ex) {
            log.error("Failed to resolve affiliation", ex);
            return "";
        }
 */
    }

    /** Parses the app-wide budget year (a String) into a {@link Year}. */
/*
    private Year currentBudgetYear() {
        String value = yearContext.getYear();
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Year.parse(value.trim());
        } catch (Exception ex) {
            return null;
        }
    }
 */

    private String dateLabel(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.toLocalDate().toString();
    }

    private LocalDateTime toDateTime(LocalDate date) {
        return date == null ? null : date.atStartOfDay();
    }

    private LocalDate toLocalDate(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.toLocalDate();
    }

    private static SerializableFunction<Double, Float> toFloat() {
        return value -> value == null ? null : value.floatValue();
    }

    private static SerializableFunction<Float, Double> toDouble() {
        return value -> value == null ? null : value.doubleValue();
    }

    private <T> List<T> safeList(ThrowingSupplier<List<T>> supplier) {
        try {
            List<T> items = supplier.get();
            return items == null ? List.of() : items;
        } catch (Exception ex) {
            log.error("Failed to load selector items", ex);
            return List.of();
        }
    }

    @FunctionalInterface
    private interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    private void notifySuccess(String message) {
        Notification n = Notification.show(message, 3000, Notification.Position.BOTTOM_START);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void notifyError(String message) {
        Notification n = Notification.show(message, 5000, Notification.Position.BOTTOM_START);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    /**
     * Filter criteria for the Person and Program columns, combined with AND.
     * Blank criteria match everything. Non-static so it can reuse the view's
     * label helpers ({@link #personLabel}, {@link #programLabel}).
     */
    private class PhDFilter {
        private String person;
        private String program;
        // null = any, true = active only, false = not-active only (has a
        // dissertation date or the inactive flag set).
        private Boolean active;

        boolean test(PhDPosition p) {
            return matches(person, personLabel(p))
                    && matches(program, programLabel(p))
                    && matchesActive(p);
        }

        private boolean matchesActive(PhDPosition p) {
            return active == null || active == isActive(p);
        }

        private boolean matches(String needle, String value) {
            if (needle == null || needle.isBlank()) {
                return true;
            }
            return value != null
                    && value.toLowerCase().contains(needle.toLowerCase().trim());
        }
    }
}
