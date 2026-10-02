package se.uu.ebc.bemanning.ui;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
//import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import se.uu.ebc.bemanning.entity.course.Course;
import se.uu.ebc.bemanning.entity.course.UGAItem;
import se.uu.ebc.bemanning.entity.courseinstance.CourseInstance;
import se.uu.ebc.bemanning.entity.courseinstance.TeachingInstance;
import se.uu.ebc.bemanning.entity.staff.Staff;
import se.uu.ebc.bemanning.service.CourseService;
import se.uu.ebc.bemanning.service.StaffService;

import com.vaadin.flow.shared.Registration;

import lombok.extern.slf4j.Slf4j;

/**
 * Server-side Vaadin view for editing {@link CourseInstance} entities with a
 * buffered inline grid editor, mirroring the other admin views.
 *
 * <p>Data access goes through {@link CourseService} (instances and the course
 * selector); the course-leader selector is backed by {@link StaffService}.
 * New rows are created as {@link TeachingInstance} (the plain course instance
 * subclass). The header row provides filtering on <em>Course group</em> and
 * <em>Course</em>.
 */
@Route(value = "courseinstances", layout = MainLayout.class)
@PageTitle("Course instances")
@Menu(order = 2, icon = "icons/pencil.svg", title = "Kurstillfällen")
@AnonymousAllowed // Vaadin navigation access control requires an explicit access
                  // annotation. Write actions remain guarded by @PreAuthorize.
@Slf4j
public class CourseInstanceView extends VerticalLayout {

    private final CourseService courseService;
    private final StaffService staffService;
    private final YearContext yearContext;

    private final Grid<CourseInstance> grid = new Grid<>(CourseInstance.class, false);
    private final Binder<CourseInstance> binder = new Binder<>(CourseInstance.class);
    private final Editor<CourseInstance> editor = grid.getEditor();

    private final List<CourseInstance> instances = new ArrayList<>();
    private GridListDataView<CourseInstance> dataView;

    // Per-column filter criteria (course group + course), AND-ed together.
    private final InstanceFilter filter = new InstanceFilter();

    private final Button newButton = new Button("Nytt kurstillfälle");

    private CourseInstance pendingNew;

    public CourseInstanceView(CourseService courseService, StaffService staffService,
                              YearContext yearContext) {
        this.courseService = courseService;
        this.staffService = staffService;
        this.yearContext = yearContext;
        setSizeFull();

        configureGrid();
        configureEditor();
        add(buildToolbar(), grid);
        loadInstances();

        // Reload for the selected year whenever it changes; unregister on detach
        // so the session-scoped YearContext does not retain a detached view.
        addAttachListener(attach -> {
            Registration reg = yearContext.addYearChangeListener(year -> {
                if (editor.isOpen()) {
                    editor.cancel();
                }
                loadInstances();
            });
            addDetachListener(detach -> reg.remove());
        });
    }

    private HorizontalLayout buildToolbar() {
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newButton.addClickListener(e -> addInstance());
        return new HorizontalLayout(newButton);
    }

    private void configureGrid() {
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();

        // Read-only Course group (derived from the course).
        Grid.Column<CourseInstance> groupCol = grid.addColumn(this::courseGroupLabel)
                .setHeader("Kursgrupp").setSortable(true).setAutoWidth(true);
        Grid.Column<CourseInstance> courseCol = grid.addColumn(this::courseLabel)
                .setHeader("Kurs").setSortable(true).setAutoWidth(true);
        Grid.Column<CourseInstance> instanceCodeCol = grid.addColumn(CourseInstance::getInstanceCode)
                .setHeader("Tillfälleskod").setAutoWidth(true);
        Grid.Column<CourseInstance> yearCol = grid.addColumn(CourseInstance::getYear)
                .setHeader("År").setSortable(true).setAutoWidth(true);
        Grid.Column<CourseInstance> extraCol = grid.addColumn(CourseInstance::getExtraDesignation)
                .setHeader("Extra benämning").setAutoWidth(true);
        Grid.Column<CourseInstance> leaderCol = grid.addColumn(this::leaderLabel)
                .setHeader("Kursledare").setAutoWidth(true);
//         Grid.Column<CourseInstance> startCol = grid.addColumn(CourseInstance::getStartDate)
//                 .setHeader("Start").setAutoWidth(true);
//         Grid.Column<CourseInstance> endCol = grid.addColumn(CourseInstance::getEndDate)
//                 .setHeader("End").setAutoWidth(true);
        Grid.Column<CourseInstance> noteCol = grid.addColumn(CourseInstance::getNote)
                .setHeader("Anteckningar").setAutoWidth(true);

        Grid.Column<CourseInstance> actionsCol = grid.addComponentColumn(this::buildRowActions)
                .setHeader("Handling").setAutoWidth(true).setFlexGrow(0);

        buildEditorComponents(courseCol, instanceCodeCol, yearCol, extraCol, leaderCol,
                /* startCol, endCol, */ noteCol, actionsCol);

        buildFilterRow(groupCol, courseCol);
    }

    /* ---- Editor ---- */

    private void buildEditorComponents(Grid.Column<CourseInstance> courseCol,
                                       Grid.Column<CourseInstance> instanceCodeCol,
                                       Grid.Column<CourseInstance> yearCol,
                                       Grid.Column<CourseInstance> extraCol,
                                       Grid.Column<CourseInstance> leaderCol,
//                                        Grid.Column<CourseInstance> startCol,
//                                        Grid.Column<CourseInstance> endCol,
                                       Grid.Column<CourseInstance> noteCol,
                                       Grid.Column<CourseInstance> actionsCol) {
        ComboBox<Course> courseField = new ComboBox<>();
        courseField.setItems(safeList(courseService::getAllCourses));
        courseField.setItemLabelGenerator(c -> c == null ? "" : courseDisplay(c));
        courseField.setWidthFull();
        // course is a UGAItem on the entity; Course is the concrete subclass.
        binder.forField(courseField)
                .asRequired("Course is required")
                .bind(ci -> asCourse(ci.getCourse()),
                        (ci, c) -> ci.setCourse(c));
        courseCol.setEditorComponent(courseField);

        TextField instanceCodeField = new TextField();
        instanceCodeField.setWidthFull();
        binder.forField(instanceCodeField)
                .bind(CourseInstance::getInstanceCode, CourseInstance::setInstanceCode);
        instanceCodeCol.setEditorComponent(instanceCodeField);

        TextField yearField = new TextField();
        yearField.setWidthFull();
        binder.forField(yearField)
                .asRequired("Year is required")
                .bind(CourseInstance::getYear, CourseInstance::setYear);
        yearCol.setEditorComponent(yearField);

        TextField extraField = new TextField();
        extraField.setWidthFull();
        binder.forField(extraField)
                .bind(CourseInstance::getExtraDesignation, CourseInstance::setExtraDesignation);
        extraCol.setEditorComponent(extraField);

        ComboBox<Staff> leaderField = new ComboBox<>();
        leaderField.setItems(safeList(staffService::getAllStaff));
        leaderField.setItemLabelGenerator(this::staffOptionLabel);
        leaderField.setWidthFull();
        binder.forField(leaderField)
                .asRequired("Course leader is required")
                .bind(CourseInstance::getCourseLeader, CourseInstance::setCourseLeader);
        leaderCol.setEditorComponent(leaderField);

/*
        DatePicker startField = new DatePicker();
        startField.setWidthFull();
        binder.forField(startField)
                .withConverter(this::toDateTime, this::toLocalDate)
                .bind(CourseInstance::getStartDate, CourseInstance::setStartDate);
        startCol.setEditorComponent(startField);

        DatePicker endField = new DatePicker();
        endField.setWidthFull();
        binder.forField(endField)
                .withConverter(this::toDateTime, this::toLocalDate)
                .bind(CourseInstance::getEndDate, CourseInstance::setEndDate);
        endCol.setEditorComponent(endField);
 */

        TextField noteField = new TextField();
        noteField.setWidthFull();
        binder.forField(noteField)
                .bind(CourseInstance::getNote, CourseInstance::setNote);
        noteCol.setEditorComponent(noteField);

        actionsCol.setEditorComponent(buildEditorActions());
    }

    private void configureEditor() {
        editor.setBinder(binder);
        editor.setBuffered(true);
        editor.addCancelListener(e -> discardPendingNew());
    }

    private HorizontalLayout buildRowActions(CourseInstance ci) {
        Button edit = new Button("Edit", e -> editItem(ci));

        Button delete = new Button("Delete", e -> delete(ci));
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        delete.setEnabled(ci.getId() != null);

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

    /* ---- Filtering (course group + course) ---- */

    private void buildFilterRow(Grid.Column<CourseInstance> groupCol,
                                Grid.Column<CourseInstance> courseCol) {
        HeaderRow filterRow = grid.appendHeaderRow();
        filterRow.getCell(groupCol).setComponent(
                textFilter("Course group", value -> filter.group = value));
        filterRow.getCell(courseCol).setComponent(
                textFilter("Course", value -> filter.course = value));
    }

    private TextField textFilter(String placeholder, java.util.function.Consumer<String> setter) {
        TextField field = new TextField();
        field.setPlaceholder(placeholder);
        field.setClearButtonVisible(true);
        field.setWidthFull();
        field.setValueChangeMode(ValueChangeMode.LAZY);
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

    /* ---- Labels / helpers ---- */

    private String courseGroupLabel(CourseInstance ci) {
        Course c = asCourse(ci.getCourse());
        return c == null || c.getCourseGroup() == null ? "" : c.getCourseGroup();
    }

    private String courseLabel(CourseInstance ci) {
        Course c = asCourse(ci.getCourse());
        return c == null ? "" : courseDisplay(c);
    }

    private String courseDisplay(Course c) {
        String code = c.getCode() == null ? "" : c.getCode();
        String name = c.getSeName() == null ? "" : c.getSeName();
        return (code + " " + name).trim();
    }

    private String leaderLabel(CourseInstance ci) {
        Staff leader = ci.getCourseLeader();
        if (leader == null || leader.getPerson() == null) {
            return "";
        }
        return leader.getPerson().getName();
    }

    private String staffOptionLabel(Staff staff) {
        if (staff == null || staff.getPerson() == null) {
            return "";
        }
        String year = staff.getYear() == null ? "" : " (" + staff.getYear() + ")";
        return staff.getPerson().getName() + year;
    }

    /** course is declared as UGAItem; narrow to Course (its concrete subtype). */
    private Course asCourse(UGAItem item) {
        return item instanceof Course course ? course : null;
    }

    private LocalDateTime toDateTime(java.time.LocalDate date) {
        return date == null ? null : date.atStartOfDay();
    }

    private java.time.LocalDate toLocalDate(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.toLocalDate();
    }

    /* ---- Data / actions ---- */

    private void editItem(CourseInstance ci) {
        if (editor.isOpen()) {
            editor.cancel();
        }
        editor.editItem(ci);
    }

    private void addInstance() {
        if (editor.isOpen()) {
            editor.cancel();
        }
        // New rows are plain TeachingInstance course instances, defaulted to the
        // currently selected year so they belong to the year in view.
        CourseInstance fresh = new TeachingInstance();
        fresh.setYear(yearContext.getYear());
        instances.add(0, fresh);
        grid.getListDataView().refreshAll();
        pendingNew = fresh;
        editor.editItem(fresh);
    }

    private void loadInstances() {
        try {
            String year = yearContext.getYear();
            instances.clear();
            // Year-scoped query (year filtering happens in the query layer).
            instances.addAll(courseService.getCourseInstancesByYear(year));
            dataView = grid.setItems(instances);
            applyFilter();
        } catch (Exception ex) {
            log.error("Failed to load course instances", ex);
            notifyError("Could not load course instances: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    private void save() {
        CourseInstance edited = editor.getItem();
        if (edited == null) {
            return;
        }
        if (!editor.save()) {
            notifyError("Please fix the highlighted fields.");
            return;
        }
        try {
            CourseInstance saved = courseService.saveCourseInstance(edited);
            pendingNew = null;
            notifySuccess("Course instance saved.");
            loadInstances();
        } catch (Exception ex) {
            log.error("Failed to save course instance", ex);
            notifyError("Could not save course instance: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    private void delete(CourseInstance ci) {
        if (ci == null || ci.getId() == null) {
            return;
        }
        try {
            courseService.deleteCourseInstance(ci.getId());
            notifySuccess("Course instance deleted.");
            loadInstances();
        } catch (Exception ex) {
            log.error("Failed to delete course instance", ex);
            notifyError("Could not delete course instance: " + ex.getMessage());
        }
    }

    private void discardPendingNew() {
        if (pendingNew != null) {
            instances.remove(pendingNew);
            pendingNew = null;
            grid.getListDataView().refreshAll();
        }
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
     * Filter criteria for the course group and course columns, combined with AND.
     * Blank criteria match everything.
     */
    private class InstanceFilter {
        private String group;
        private String course;

        boolean test(CourseInstance ci) {
            return matches(group, courseGroupLabel(ci))
                    && matches(course, courseLabel(ci));
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
