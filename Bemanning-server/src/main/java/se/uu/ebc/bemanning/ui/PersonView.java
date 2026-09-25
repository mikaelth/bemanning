package se.uu.ebc.bemanning.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import se.uu.ebc.bemanning.enums.UserRoles;
import se.uu.ebc.bemanning.service.PeopleService;
import se.uu.ebc.bemanning.entity.Person;

import lombok.extern.slf4j.Slf4j;

/**
 * Server-side Vaadin view for editing {@link Person} entities, mirroring
 * {@link CourseView}.
 *
 * <p>Renders an editable Grid backed by {@link PeopleService} (in-process, no
 * REST hop). Editing happens inline using Vaadin's <em>buffered</em> editor:
 * clicking <em>Edit</em> turns the row's cells into input fields, and the
 * changes are only written back to the {@link Person} bean (and persisted) when
 * <em>Save</em> is pressed. <em>Cancel</em> discards the buffered edits.
 *
 * <p>Write actions are guarded by {@code @PreAuthorize(ROLE_COREDATAADMIN)},
 * matching the person administration authorization used elsewhere.
 */
// The Vaadin servlet is mapped to the root context (/*), so this route resolves
// to /people. (Spring Data REST is served under /api, so there is no collision.)
@Route("people")
@PageTitle("Personer")
@AnonymousAllowed // Vaadin navigation access control requires an explicit access
                  // annotation; without one the route is denied (HTTP 403). The
                  // write actions remain guarded by @PreAuthorize(ROLE_COREDATAADMIN).
@Slf4j
public class PersonView extends VerticalLayout {

    private final PeopleService peopleService;

    private final Grid<Person> grid = new Grid<>(Person.class, false);
    // Column-level binder used by the buffered editor. In buffered mode the
    // editor validates and writes into the edited bean only when save() is
    // called, so we can commit or discard the row's edits atomically.
    private final Binder<Person> binder = new Binder<>(Person.class);
    private final Editor<Person> editor = grid.getEditor();

    // Backing list so newly-added (unsaved) rows can be dropped on cancel.
    private final List<Person> people = new ArrayList<>();
    // In-memory list data view captured from grid.setItems(...); used to apply
    // the per-column filters.
    private GridListDataView<Person> dataView;

    // "All" sentinel for the three-state boolean filters.
    private static final String FILTER_ALL = "(alla)";
    // Holds the current per-column filter criteria; PersonFilter#test AND-s them.
    private final PersonFilter filter = new PersonFilter();

    private final Button newButton = new Button("New person");

    // Tracks a row that was added via "New person" but not yet persisted, so a
    // cancelled edit removes it from the grid instead of leaving an empty row.
    private Person pendingNew;

    public PersonView(PeopleService peopleService) {
        this.peopleService = peopleService;
        setSizeFull();

        configureGrid();
        configureEditor();
        add(buildToolbar(), grid);
        loadPeople();
    }

    private HorizontalLayout buildToolbar() {
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newButton.addClickListener(e -> addPerson());
        return new HorizontalLayout(newButton);
    }

    private void configureGrid() {
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();

        // Read-only Id column. The id is database-generated, so it is displayed
        // but never given an editor component - it stays read-only even while a
        // row is being edited. New (unsaved) rows show it blank until saved.
//         grid.addColumn(Person::getId)
//                 .setHeader("Id").setSortable(true).setAutoWidth(true).setFlexGrow(0);

        // Display columns, each supplied with an editor component below.
        Grid.Column<Person> activeCol = grid.addColumn(p -> p.isActive() ? "Ja" : "Nej")
                .setHeader("Aktiv").setAutoWidth(true);
        Grid.Column<Person> usernameCol = grid.addColumn(Person::getUsername)
                .setHeader("AKKA id").setSortable(true).setAutoWidth(true);
        Grid.Column<Person> givenNameCol = grid.addColumn(Person::getGivenName)
                .setHeader("Förnamn").setSortable(true).setAutoWidth(true);
        Grid.Column<Person> familyNameCol = grid.addColumn(Person::getFamilyName)
                .setHeader("Efternamn").setSortable(true).setAutoWidth(true);
        Grid.Column<Person> familyFirstCol = grid.addColumn(p -> p.isFamilyFirst() ? "Ja" : "Nej")
                .setHeader("Familjenamn först").setAutoWidth(true);
        Grid.Column<Person> rolesCol = grid.addColumn(this::rolesLabel)
                .setHeader("Behörigheter").setAutoWidth(true);
        Grid.Column<Person> noteCol = grid.addColumn(Person::getNote)
                .setHeader("Anteckningar").setAutoWidth(true);

        Grid.Column<Person> actionsCol = grid.addComponentColumn(this::buildRowActions)
                .setHeader("").setAutoWidth(true).setFlexGrow(0);

        buildEditorComponents( activeCol, usernameCol, givenNameCol, familyNameCol, familyFirstCol,
               rolesCol, noteCol, actionsCol);

        buildFilterRow(activeCol, usernameCol, givenNameCol, familyNameCol, familyFirstCol,
                rolesCol, noteCol);
    }

    /**
     * Adds a filter row beneath the header with one filter field per data
     * column. Each field updates {@link #filter} and re-runs the combined
     * predicate on the grid's {@link com.vaadin.flow.data.provider.ListDataView}.
     * All active column filters are AND-ed together.
     */
    private void buildFilterRow(Grid.Column<Person> activeCol,
                                Grid.Column<Person> usernameCol,
                                Grid.Column<Person> givenNameCol,
                                Grid.Column<Person> familyNameCol,
                                Grid.Column<Person> familyFirstCol,
                                Grid.Column<Person> rolesCol,
                                Grid.Column<Person> noteCol) {
        HeaderRow filterRow = grid.appendHeaderRow();

        filterRow.getCell(activeCol).setComponent(
                booleanFilter(value -> filter.active = value));
        filterRow.getCell(usernameCol).setComponent(
                textFilter("AKKA id", value -> filter.username = value));
        filterRow.getCell(givenNameCol).setComponent(
                textFilter("Förnamn", value -> filter.givenName = value));
        filterRow.getCell(familyNameCol).setComponent(
                textFilter("Efternamn", value -> filter.familyName = value));
        filterRow.getCell(familyFirstCol).setComponent(
                booleanFilter(value -> filter.familyFirst = value));
        filterRow.getCell(rolesCol).setComponent(buildRolesFilter());
        filterRow.getCell(noteCol).setComponent(
                textFilter("Anteckningar", value -> filter.note = value));
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

    /** A three-state Select (All / Ja / Nej) mapped to a nullable Boolean. */
    private Select<String> booleanFilter(java.util.function.Consumer<Boolean> setter) {
        Select<String> select = new Select<>();
        select.setItems(FILTER_ALL, "Ja", "Nej");
        select.setValue(FILTER_ALL);
        select.setWidthFull();
        select.addValueChangeListener(e -> {
            String v = e.getValue();
            setter.accept(FILTER_ALL.equals(v) || v == null ? null : "Ja".equals(v));
            applyFilter();
        });
        return select;
    }

    /** A multi-select of roles; a row matches if it has all selected roles. */
    private MultiSelectComboBox<UserRoles> buildRolesFilter() {
        MultiSelectComboBox<UserRoles> field = new MultiSelectComboBox<>();
        field.setItems(UserRoles.values());
        field.setPlaceholder("Behörigheter");
        field.setWidthFull();
        field.addValueChangeListener(e -> {
            filter.roles = e.getValue();
            applyFilter();
        });
        return field;
    }

    private void applyFilter() {
        if (dataView != null) {
            dataView.setFilter(filter::test);
        }
    }

    /**
     * Wires the per-column editor fields and their bindings. This is where the
     * buffered editing behaviour is set up: {@code binder.forField(...)} defines
     * how each field reads from and writes to the {@link Person} bean, but the
     * write only happens on {@link Editor#save()}.
     */
    private void buildEditorComponents(Grid.Column<Person> activeCol,
    								   Grid.Column<Person> usernameCol,
 									   Grid.Column<Person> givenNameCol,
                                       Grid.Column<Person> familyNameCol,
                                       Grid.Column<Person> familyFirstCol,
                                       Grid.Column<Person> rolesCol,
                                       Grid.Column<Person> noteCol,
                                       Grid.Column<Person> actionsCol) {

        Checkbox activeField = new Checkbox();
        binder.forField(activeField)
                .bind(Person::isActive, Person::setActive);
        activeCol.setEditorComponent(activeField);

        TextField usernameField = new TextField();
        usernameField.setWidthFull();
        binder.forField(usernameField)
                .asRequired("Username is required")
                .bind(Person::getUsername, Person::setUsername);
        usernameCol.setEditorComponent(usernameField);

        TextField givenNameField = new TextField();
        givenNameField.setWidthFull();
        binder.forField(givenNameField)
                .asRequired("Given name is required")
                .bind(Person::getGivenName, Person::setGivenName);
        givenNameCol.setEditorComponent(givenNameField);

        TextField familyNameField = new TextField();
        familyNameField.setWidthFull();
        binder.forField(familyNameField)
                .asRequired("Family name is required")
                .bind(Person::getFamilyName, Person::setFamilyName);
        familyNameCol.setEditorComponent(familyNameField);

        Checkbox familyFirstField = new Checkbox();
        binder.forField(familyFirstField)
                .bind(Person::isFamilyFirst, Person::setFamilyFirst);
        familyFirstCol.setEditorComponent(familyFirstField);

        MultiSelectComboBox<UserRoles> rolesField = new MultiSelectComboBox<>();
        rolesField.setItems(UserRoles.values());
        rolesField.setWidthFull();
        // The bean uses a Set<UserRoles>; MultiSelectComboBox's value type is
        // also Set<UserRoles>, so bind directly (no converter needed). Copy into
        // a fresh set on write so the editor does not alias the bean's set.
        binder.forField(rolesField)
                .bind(
                        person -> person.getUserRoles() == null
                                ? Set.of()
                                : Set.copyOf(person.getUserRoles()),
                        (person, selection) -> person.setUserRoles(
                                selection == null ? new java.util.HashSet<>() : new java.util.HashSet<>(selection)));
        rolesCol.setEditorComponent(rolesField);

        TextField noteField = new TextField();
        noteField.setWidthFull();
        binder.forField(noteField)
                .bind(Person::getNote, Person::setNote);
        noteCol.setEditorComponent(noteField);

        actionsCol.setEditorComponent(buildEditorActions());
    }

    private void configureEditor() {
        editor.setBinder(binder);
        editor.setBuffered(true);
        // If the user starts editing another row (or the buffered edit is
        // otherwise abandoned) drop a not-yet-saved new row so the grid does
        // not keep an orphaned blank entry.
        editor.addCancelListener(e -> discardPendingNew());
        // NOTE: do NOT call DataProvider.refreshAll() from the editor's open/close
        // listeners. Refreshing the items re-renders (and detaches) the row the
        // editor is attached to, which immediately closes the editor again.
    }

    /** Per-row "Edit"/"Delete" buttons shown when the row is not being edited. */
    private HorizontalLayout buildRowActions(Person person) {
        Button edit = new Button("Edit", e -> editItem(person));

        Button delete = new Button("Delete", e -> delete(person));
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        delete.setEnabled(person.getId() != null);

        HorizontalLayout actions = new HorizontalLayout(edit, delete);
        actions.setPadding(false);
        return actions;
    }

    /** "Save"/"Cancel" buttons shown inside the row while it is being edited. */
    private HorizontalLayout buildEditorActions() {
        Button save = new Button("Save", e -> save());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);

        Button cancel = new Button("Cancel", e -> editor.cancel());

        HorizontalLayout actions = new HorizontalLayout(save, cancel);
        actions.setPadding(false);
        return actions;
    }

    private String rolesLabel(Person person) {
        if (person.getUserRoles() == null || person.getUserRoles().isEmpty()) {
            return "";
        }
        return person.getUserRoles().stream()
                .map(Enum::name)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    /**
     * Opens the buffered editor on the given row. Only one row can be edited at
     * a time, so any edit already in progress is cancelled first (which also
     * drops a not-yet-saved new row via the cancel listener).
     */
    private void editItem(Person person) {
        if (editor.isOpen()) {
            editor.cancel();
        }
        editor.editItem(person);
    }

    private void addPerson() {
        // Cancel any edit in progress first (this also discards a previous
        // pending-new row via the cancel listener) before adding the new one.
        if (editor.isOpen()) {
            editor.cancel();
        }
        Person fresh = new Person();
        // Add the row so the editor has something to attach to, then open it.
        people.add(0, fresh);
        grid.getListDataView().refreshAll();
        pendingNew = fresh;
        editor.editItem(fresh);
    }

    private void loadPeople() {
        try {
            people.clear();
            people.addAll(peopleService.getAllPersons());
            dataView = grid.setItems(people);
            // Re-apply any active column filters to the freshly loaded data.
            applyFilter();
        } catch (Exception ex) {
            log.error("Failed to load people", ex);
            notifyError("Could not load people: " + ex.getMessage());
        }
    }

    /**
     * Commits the buffered editor and persists the edited person.
     *
     * <p>{@link Editor#save()} runs the binder validation and, only if valid,
     * writes the field values into the edited bean. We then persist that bean
     * and refresh the grid.
     */
    @PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    private void save() {
        Person edited = editor.getItem();
        if (edited == null) {
            return;
        }
        // save() returns false and keeps the editor open when validation fails.
        if (!editor.save()) {
            notifyError("Please fix the highlighted fields.");
            return;
        }
        try {
            Person saved = peopleService.savePerson(edited);
            // This row is now persisted, so it is no longer a pending-new row.
            pendingNew = null;
            notifySuccess("Person saved: " + saved.getName());
            loadPeople();
        } catch (Exception ex) {
            log.error("Failed to save person", ex);
            notifyError("Could not save person: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    private void delete(Person person) {
        if (person == null || person.getId() == null) {
            return;
        }
        try {
            peopleService.deletePerson(person.getId());
            notifySuccess("Person deleted.");
            loadPeople();
        } catch (Exception ex) {
            log.error("Failed to delete person", ex);
            notifyError("Could not delete person: " + ex.getMessage());
        }
    }

    /** Removes an added-but-never-saved row when its edit is cancelled. */
    private void discardPendingNew() {
        if (pendingNew != null) {
            people.remove(pendingNew);
            pendingNew = null;
            grid.getListDataView().refreshAll();
        }
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
     * Holds the current per-column filter criteria and combines them with AND
     * semantics. A null/blank criterion means "no filter" for that column.
     */
    private static class PersonFilter {
        private String username;
        private String givenName;
        private String familyName;
        private String note;
        private Boolean active;      // null = any, true = Ja, false = Nej
        private Boolean familyFirst; // null = any, true = Ja, false = Nej
        private Set<UserRoles> roles = Set.of();

        boolean test(Person person) {
            return matchesText(username, person.getUsername())
                    && matchesText(givenName, person.getGivenName())
                    && matchesText(familyName, person.getFamilyName())
                    && matchesText(note, person.getNote())
                    && matchesBoolean(active, person.isActive())
                    && matchesBoolean(familyFirst, person.isFamilyFirst())
                    && matchesRoles(person);
        }

        private boolean matchesText(String needle, String value) {
            if (needle == null || needle.isBlank()) {
                return true;
            }
            return value != null
                    && value.toLowerCase().contains(needle.toLowerCase().trim());
        }

        private boolean matchesBoolean(Boolean wanted, boolean value) {
            return wanted == null || wanted == value;
        }

        private boolean matchesRoles(Person person) {
            if (roles == null || roles.isEmpty()) {
                return true;
            }
            // Match rows that have every selected role.
            return person.getUserRoles() != null
                    && person.getUserRoles().containsAll(roles);
        }
    }
}
