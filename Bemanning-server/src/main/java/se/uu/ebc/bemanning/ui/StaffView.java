package se.uu.ebc.bemanning.ui;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import se.uu.ebc.bemanning.entity.OrganisationUnit;
import se.uu.ebc.bemanning.entity.Person;
import se.uu.ebc.bemanning.entity.staff.ExternalStaff;
import se.uu.ebc.bemanning.entity.staff.Staff;
import se.uu.ebc.bemanning.enums.EmploymentType;
import com.vaadin.flow.shared.Registration;

import se.uu.ebc.bemanning.service.OrganisationUnitService;
import se.uu.ebc.bemanning.service.PeopleService;
import se.uu.ebc.bemanning.service.StaffService;

import lombok.extern.slf4j.Slf4j;

/**
 * Server-side Vaadin view for editing {@link Staff} entities with a buffered
 * inline grid editor, mirroring {@link CourseView} / {@link PersonView}.
 *
 * <p>The grid lists all staff (both {@code AkkaStaff} and {@code ExternalStaff}),
 * but only {@link ExternalStaff} rows are editable - the AKKA-sourced rows are
 * maintained from the directory and are shown read-only here (their Edit button
 * is disabled). Adding a new row always creates an {@code ExternalStaff}.
 *
 * <p>Data access goes through {@link StaffService}; the person and department
 * selectors are backed by {@link PeopleService} and {@link OrganisationUnitService}.
 */
@Route(value = "staff", layout = MainLayout.class)
@PageTitle("Staff")
@Menu(order = 3, icon = "icons/users.svg", title = "Personal")
@AnonymousAllowed // Vaadin navigation access control requires an explicit access
                  // annotation. Write actions remain guarded by @PreAuthorize.
@Slf4j
public class StaffView extends VerticalLayout {

    private final StaffService staffService;
    private final PeopleService peopleService;
    private final OrganisationUnitService organisationUnitService;
    private final YearContext yearContext;

    private final Grid<Staff> grid = new Grid<>(Staff.class, false);
    private final Binder<Staff> binder = new Binder<>(Staff.class);
    private final Editor<Staff> editor = grid.getEditor();

    private final List<Staff> staff = new ArrayList<>();
    // In-memory list data view captured from grid.setItems(...); used to apply
    // the per-column filters.
    private GridListDataView<Staff> dataView;

    // Holds the current filter criteria; StaffFilter#test AND-s them.
    private final StaffFilter filter = new StaffFilter();

    private final Button newButton = new Button("Ny extern personal");

    // Tracks a row added via "New" but not yet persisted, so a cancelled edit
    // removes it from the grid instead of leaving an empty row.
    private Staff pendingNew;

    public StaffView(StaffService staffService,
                     PeopleService peopleService,
                     OrganisationUnitService organisationUnitService,
                     YearContext yearContext) {
        this.staffService = staffService;
        this.peopleService = peopleService;
        this.organisationUnitService = organisationUnitService;
        this.yearContext = yearContext;
        setSizeFull();

        configureGrid();
        configureEditor();
        add(buildToolbar(), grid);
        loadStaff();

        // Reload for the selected year whenever it changes; unregister on detach
        // so the session-scoped YearContext does not retain a detached view.
        addAttachListener(attach -> {
            Registration reg = yearContext.addYearChangeListener(year -> {
                // Cancel any in-progress edit before swapping out the data.
                if (editor.isOpen()) {
                    editor.cancel();
                }
                loadStaff();
            });
            addDetachListener(detach -> reg.remove());
        });
    }

    private HorizontalLayout buildToolbar() {
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newButton.addClickListener(e -> addStaff());
        return new HorizontalLayout(newButton);
    }

    private void configureGrid() {
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();

        // Read-only Type column so it is clear which rows are editable.
        Grid.Column<Staff> typeCol = grid.addColumn(this::typeLabel)
                .setHeader("Kategori").setAutoWidth(true).setFlexGrow(0);

        Grid.Column<Staff> personCol = grid.addColumn(this::personLabel)
                .setHeader("Person").setSortable(true).setAutoWidth(true);
        Grid.Column<Staff> ouCol = grid.addColumn(this::ouLabel)
                .setHeader("Enhet").setAutoWidth(true);
        Grid.Column<Staff> positionCol = grid.addColumn(s -> s.getPosition() == null ? "" : s.getPosition().name())
                .setHeader("Anställning").setAutoWidth(true);
        Grid.Column<Staff> yearCol = grid.addColumn(Staff::getYear)
                .setHeader("År").setSortable(true).setAutoWidth(true);
        Grid.Column<Staff> percentGuCol = grid.addColumn(Staff::getPercentGU)
                .setHeader("% GU").setAutoWidth(true);
        Grid.Column<Staff> hourlyChargeCol = grid.addColumn(Staff::getHourlyCharge)
                .setHeader("Timkostnad").setAutoWidth(true);
        Grid.Column<Staff> ibCol = grid.addColumn(Staff::getIb)
                .setHeader("IB").setAutoWidth(true);
        Grid.Column<Staff> programCol = grid.addColumn(Staff::getProgram)
                .setHeader("Program").setAutoWidth(true);
        Grid.Column<Staff> noteCol = grid.addColumn(Staff::getNote)
                .setHeader("Anteckningar").setAutoWidth(true);

        Grid.Column<Staff> actionsCol = grid.addComponentColumn(this::buildRowActions)
                .setHeader("Handling").setAutoWidth(true).setFlexGrow(0);

        buildEditorComponents(personCol, ouCol, positionCol, yearCol, percentGuCol,
                hourlyChargeCol, ibCol, programCol, noteCol, actionsCol);

        buildFilterRow(typeCol, personCol, ouCol, positionCol, yearCol);
    }

    /**
     * Adds a filter row beneath the header with filter fields for Type, Person,
     * Dept, Position and Year. All active filters are AND-ed together and applied
     * to the grid's ListDataView.
     */
    private void buildFilterRow(Grid.Column<Staff> typeCol,
                                Grid.Column<Staff> personCol,
                                Grid.Column<Staff> ouCol,
                                Grid.Column<Staff> positionCol,
                                Grid.Column<Staff> yearCol) {
        com.vaadin.flow.component.grid.HeaderRow filterRow = grid.appendHeaderRow();

        // Type: All / External / AKKA.
        ComboBox<String> typeSelect = new ComboBox<>();
        typeSelect.setItems("External", "AKKA");
        typeSelect.setPlaceholder("Type");
        typeSelect.setClearButtonVisible(true);
        typeSelect.setWidthFull();
        typeSelect.addValueChangeListener(e -> {
            filter.type = e.getValue();
            applyFilter();
        });
        filterRow.getCell(typeCol).setComponent(typeSelect);

        filterRow.getCell(personCol).setComponent(
                textFilter("Person", value -> filter.person = value));
        filterRow.getCell(ouCol).setComponent(
                textFilter("Dept", value -> filter.dept = value));

        // Position: All / each EmploymentType.
        ComboBox<EmploymentType> positionSelect = new ComboBox<>();
        positionSelect.setItems(EmploymentType.values());
        positionSelect.setPlaceholder("Position");
        positionSelect.setClearButtonVisible(true);
        positionSelect.setWidthFull();
        positionSelect.addValueChangeListener(e -> {
            filter.position = e.getValue();
            applyFilter();
        });
        filterRow.getCell(positionCol).setComponent(positionSelect);

        filterRow.getCell(yearCol).setComponent(
                textFilter("Year", value -> filter.year = value));
    }

    /** A text field that runs {@code setter} then re-applies the combined filter. */
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

    private String typeLabel(Staff s) {
        return s instanceof ExternalStaff ? "External" : "AKKA";
    }

    /**
     * Wires the per-column editor fields and their bindings. Person and dept
     * selectors are backed by PeopleService / OrganisationUnitService.
     */
    private void buildEditorComponents(Grid.Column<Staff> personCol,
                                       Grid.Column<Staff> ouCol,
                                       Grid.Column<Staff> positionCol,
                                       Grid.Column<Staff> yearCol,
                                       Grid.Column<Staff> percentGuCol,
                                       Grid.Column<Staff> hourlyChargeCol,
                                       Grid.Column<Staff> ibCol,
                                       Grid.Column<Staff> programCol,
                                       Grid.Column<Staff> noteCol,
                                       Grid.Column<Staff> actionsCol) {
        ComboBox<Person> personField = new ComboBox<>();
        personField.setItems(safeList(peopleService::getAllPersons));
        personField.setItemLabelGenerator(p -> p == null ? "" : p.getName());
        personField.setWidthFull();
        binder.forField(personField)
                .asRequired("Person är nödvändig")
                .bind(Staff::getPerson, Staff::setPerson);
        personCol.setEditorComponent(personField);

        ComboBox<OrganisationUnit> ouField = new ComboBox<>();
        ouField.setItems(safeList(organisationUnitService::getAllOrganisationUnits));
        ouField.setItemLabelGenerator(ou -> ou == null ? "" : ou.getAbbreviation());
        ouField.setWidthFull();
        binder.forField(ouField)
                .asRequired("Department is required")
                .bind(Staff::getOrganisationUnit, Staff::setOrganisationUnit);
        ouCol.setEditorComponent(ouField);

        ComboBox<EmploymentType> positionField = new ComboBox<>();
        positionField.setItems(EmploymentType.values());
        positionField.setWidthFull();
        binder.forField(positionField)
                .bind(Staff::getPosition, Staff::setPosition);
        positionCol.setEditorComponent(positionField);

        TextField yearField = new TextField();
        yearField.setWidthFull();
        binder.forField(yearField)
                .bind(Staff::getYear, Staff::setYear);
        yearCol.setEditorComponent(yearField);

        NumberField percentGuField = new NumberField();
        percentGuField.setWidthFull();
        binder.forField(percentGuField)
                .withConverter(toFloat(), toDouble())
                .bind(Staff::getPercentGU, Staff::setPercentGU);
        percentGuCol.setEditorComponent(percentGuField);

        NumberField hourlyChargeField = new NumberField();
        hourlyChargeField.setWidthFull();
        binder.forField(hourlyChargeField)
                .withConverter(toFloat(), toDouble())
                .bind(Staff::getHourlyCharge, Staff::setHourlyCharge);
        hourlyChargeCol.setEditorComponent(hourlyChargeField);

        NumberField ibField = new NumberField();
        ibField.setWidthFull();
        binder.forField(ibField)
                .withConverter(toFloat(), toDouble())
                .bind(Staff::getIb, Staff::setIb);
        ibCol.setEditorComponent(ibField);

        TextField programField = new TextField();
        programField.setWidthFull();
        binder.forField(programField)
                .bind(Staff::getProgram, Staff::setProgram);
        programCol.setEditorComponent(programField);

        TextField noteField = new TextField();
        noteField.setWidthFull();
        binder.forField(noteField)
                .bind(Staff::getNote, Staff::setNote);
        noteCol.setEditorComponent(noteField);

        actionsCol.setEditorComponent(buildEditorActions());
    }

    private void configureEditor() {
        editor.setBinder(binder);
        editor.setBuffered(true);
        editor.addCancelListener(e -> discardPendingNew());
        // NOTE: do NOT call DataProvider.refreshAll() from open/close listeners -
        // it detaches the edited row and closes the editor immediately.
    }

    /** Per-row "Edit"/"Delete". Only ExternalStaff rows can be edited. */
    private HorizontalLayout buildRowActions(Staff s) {
        boolean editable = s instanceof ExternalStaff;

        Button edit = new Button("Edit", e -> {
            if (editable) {
                editItem(s);
            }
        });
        edit.setEnabled(editable);

        Button delete = new Button("Delete", e -> delete(s));
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        // Only persisted external staff can be deleted here.
        delete.setEnabled(editable && s.getId() != null);

        HorizontalLayout actions = new HorizontalLayout(edit, delete);
        actions.setPadding(false);
        return actions;
    }

    private HorizontalLayout buildEditorActions() {
        Button save = new Button("Save", e -> save());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);

        Button cancel = new Button("Cancel", e -> editor.cancel());

        HorizontalLayout actions = new HorizontalLayout(save, cancel);
        actions.setPadding(false);
        return actions;
    }

    private String personLabel(Staff s) {
        return s.getPerson() == null ? "" : s.getPerson().getName();
    }

    private String ouLabel(Staff s) {
        return s.getOrganisationUnit() == null ? "" : s.getOrganisationUnit().getAbbreviation();
    }

    /** Opens the buffered editor, cancelling any edit already in progress. */
    private void editItem(Staff s) {
        if (editor.isOpen()) {
            editor.cancel();
        }
        editor.editItem(s);
    }

    private void addStaff() {
        if (editor.isOpen()) {
            editor.cancel();
        }
        // New rows are always ExternalStaff, the only editable subclass.
        ExternalStaff fresh = new ExternalStaff();
        // Default the year to the currently selected one so the new row belongs
        // to the year in view (and remains visible after save).
        fresh.setYear(yearContext.getYear());
        staff.add(0, fresh);
        grid.getListDataView().refreshAll();
        pendingNew = fresh;
        editor.editItem(fresh);
    }

    private void loadStaff() {
        try {
            String year = yearContext.getYear();
            staff.clear();
            // Year-scoped query (year filtering happens in the query layer).
            staff.addAll(staffService.getStaffByYear(year));
            dataView = grid.setItems(staff);
            // Re-apply any active column filters to the freshly loaded data.
            applyFilter();
        } catch (Exception ex) {
            log.error("Failed to load staff", ex);
            notifyError("Could not load staff: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    private void save() {
        Staff edited = editor.getItem();
        if (edited == null) {
            return;
        }
        // Guard: only ExternalStaff is editable here.
        if (!(edited instanceof ExternalStaff)) {
            editor.cancel();
            notifyError("Only external staff can be edited.");
            return;
        }
        if (!editor.save()) {
            notifyError("Please fix the highlighted fields.");
            return;
        }
        try {
            Staff saved = staffService.saveStaff(edited);
            pendingNew = null;
            notifySuccess("Staff saved: " + personLabel(saved));
            loadStaff();
        } catch (Exception ex) {
            log.error("Failed to save staff", ex);
            notifyError("Could not save staff: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    private void delete(Staff s) {
        if (s == null || s.getId() == null || !(s instanceof ExternalStaff)) {
            return;
        }
        try {
            staffService.deleteStaff(s.getId());
            notifySuccess("Staff deleted.");
            loadStaff();
        } catch (Exception ex) {
            log.error("Failed to delete staff", ex);
            notifyError("Could not delete staff: " + ex.getMessage());
        }
    }

    /** Removes an added-but-never-saved row when its edit is cancelled. */
    private void discardPendingNew() {
        if (pendingNew != null) {
            staff.remove(pendingNew);
            pendingNew = null;
            grid.getListDataView().refreshAll();
        }
    }

    /** Runs a list supplier, returning an empty list (and logging) on failure. */
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

    /** Presentation (Double from NumberField) -> model (Float on Staff). */
    private static com.vaadin.flow.function.SerializableFunction<Double, Float> toFloat() {
        return value -> value == null ? null : value.floatValue();
    }

    /** Model (Float on Staff) -> presentation (Double for NumberField). */
    private static com.vaadin.flow.function.SerializableFunction<Float, Double> toDouble() {
        return value -> value == null ? null : value.doubleValue();
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
     * Holds the current filter criteria for Type, Person, Dept, Position and
     * Year, combined with AND semantics. A null/blank criterion means "no
     * filter" for that column.
     */
    private class StaffFilter {
        private String type;               // "External" / "AKKA" / null
        private String person;             // contains (case-insensitive)
        private String dept;               // contains (case-insensitive)
        private EmploymentType position;   // exact, or null for any
        private String year;               // contains (case-insensitive)

        boolean test(Staff s) {
            return matchesType(s)
                    && matchesText(person, personLabel(s))
                    && matchesText(dept, ouLabel(s))
                    && matchesPosition(s)
                    && matchesText(year, s.getYear());
        }

        private boolean matchesType(Staff s) {
            return type == null || type.isBlank() || type.equals(typeLabel(s));
        }

        private boolean matchesPosition(Staff s) {
            return position == null || position.equals(s.getPosition());
        }

        private boolean matchesText(String needle, String value) {
            if (needle == null || needle.isBlank()) {
                return true;
            }
            return value != null
                    && value.toLowerCase().contains(needle.toLowerCase().trim());
        }
    }
}
