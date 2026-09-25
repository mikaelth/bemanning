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
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import se.uu.ebc.bemanning.enums.CourseGroup;
import se.uu.ebc.bemanning.service.CourseService;
import se.uu.ebc.bemanning.entity.course.Course;

import lombok.extern.slf4j.Slf4j;

/**
 * Server-side Vaadin view that substitutes {@code CourseRestController}.
 *
 * <p>Instead of exposing {@code /rest/courses} JSON endpoints consumed by the
 * ExtJS SPA, this view renders an editable Grid and calls {@link CourseService}
 * directly (in-process). Editing happens inline in the grid using Vaadin's
 * <em>buffered</em> editor: clicking <em>Edit</em> turns the row's cells into
 * input fields, and the changes are only written back to the {@link Course}
 * bean (and persisted) when <em>Save</em> is pressed. <em>Cancel</em> discards
 * the buffered edits. The CRUD semantics and the {@code ROLE_COREDATAADMIN}
 * authorization mirror the original REST controller:
 * <ul>
 *   <li>GET  /rest/courses        &rarr; grid list (loadCourses)</li>
 *   <li>POST /rest/courses        &rarr; save (new)   - ROLE_COREDATAADMIN</li>
 *   <li>PUT  /rest/courses/{id}   &rarr; save (edit)  - ROLE_COREDATAADMIN</li>
 *   <li>DELETE /rest/courses/{id} &rarr; delete       - ROLE_COREDATAADMIN</li>
 * </ul>
 *
 * The old REST controller and ExtJS SPA are intentionally left in place; this
 * view runs in parallel at {@code /courses}.
 */
// The Vaadin servlet is mapped to the root context (/*), which is required for
// the React Router bootstrap. This route therefore resolves to /courses.
@Route("courses")
@PageTitle("Courses")
@AnonymousAllowed // Vaadin navigation access control requires an explicit access
                  // annotation; without one the route is denied (HTTP 403). The
                  // write actions remain guarded by @PreAuthorize(ROLE_COREDATAADMIN),
                  // mirroring the original CourseRestController.
@Slf4j
public class CourseView extends VerticalLayout {

    private final CourseService courseService;

    private final Grid<Course> grid = new Grid<>(Course.class, false);
    // Column-level binder used by the buffered editor. In buffered mode the
    // editor validates and writes into the edited bean only when save() is
    // called, so we can commit or discard the row's edits atomically.
    private final Binder<Course> binder = new Binder<>(Course.class);
    private final Editor<Course> editor = grid.getEditor();

    // Backing list so newly-added (unsaved) rows can be dropped on cancel.
    private final List<Course> courses = new ArrayList<>();

    private final Button newButton = new Button("New course");

    // Tracks a row that was added via "New course" but not yet persisted, so a
    // cancelled edit removes it from the grid instead of leaving an empty row.
    private Course pendingNew;

    public CourseView(CourseService courseService) {
        this.courseService = courseService;
        setSizeFull();

        configureGrid();
        configureEditor();
        add(buildToolbar(), grid);
        loadCourses();
    }

    private HorizontalLayout buildToolbar() {
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newButton.addClickListener(e -> addCourse());
        return new HorizontalLayout(newButton);
    }

    private void configureGrid() {
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();

        // Display columns, each supplied with an editor component below.
        Grid.Column<Course> codeCol = grid.addColumn(Course::getCode)
                .setHeader("Code").setSortable(true).setAutoWidth(true);
        Grid.Column<Course> seNameCol = grid.addColumn(Course::getSeName)
                .setHeader("Name (sv)").setSortable(true).setAutoWidth(true);
        Grid.Column<Course> enNameCol = grid.addColumn(Course::getEnName)
                .setHeader("Name (en)").setSortable(true).setAutoWidth(true);
        Grid.Column<Course> groupCol = grid.addColumn(this::courseGroupLabel)
                .setHeader("Group").setAutoWidth(true);
        Grid.Column<Course> creditsCol = grid.addColumn(Course::getCredits)
                .setHeader("Credits").setAutoWidth(true);

        Grid.Column<Course> actionsCol = grid.addComponentColumn(this::buildRowActions)
                .setHeader("Actions").setAutoWidth(true).setFlexGrow(0);

        buildEditorComponents(codeCol, seNameCol, enNameCol, groupCol, creditsCol, actionsCol);
    }

    /**
     * Wires the per-column editor fields and their bindings. This is where the
     * buffered editing behaviour is set up: {@code binder.forField(...)} defines
     * how each field reads from and writes to the {@link Course} bean, but the
     * write only happens on {@link Editor#save()}.
     */
    private void buildEditorComponents(Grid.Column<Course> codeCol,
                                       Grid.Column<Course> seNameCol,
                                       Grid.Column<Course> enNameCol,
                                       Grid.Column<Course> groupCol,
                                       Grid.Column<Course> creditsCol,
                                       Grid.Column<Course> actionsCol) {
        TextField codeField = new TextField();
        codeField.setWidthFull();
        binder.forField(codeField)
                .asRequired("Code is required")
                .bind(Course::getCode, Course::setCode);
        codeCol.setEditorComponent(codeField);

        TextField seNameField = new TextField();
        seNameField.setWidthFull();
        binder.forField(seNameField)
                .asRequired("Swedish name is required")
                .bind(Course::getSeName, Course::setSeName);
        seNameCol.setEditorComponent(seNameField);

        TextField enNameField = new TextField();
        enNameField.setWidthFull();
        binder.forField(enNameField)
                .bind(Course::getEnName, Course::setEnName);
        enNameCol.setEditorComponent(enNameField);

        ComboBox<CourseGroup> groupField = new ComboBox<>();
        groupField.setItems(CourseGroup.values());
        groupField.setItemLabelGenerator(CourseGroup::displayName);
        groupField.setWidthFull();
        binder.forField(groupField)
                // The COURSE.COURSE_GROUP column stores the display name (e.g.
                // "Baskurser"), matching the rest of the app (see the
                // /coursegroups REST endpoint). Convert the selected enum to its
                // display name on save, and resolve the stored display name back
                // to the enum when populating the field.
                .withConverter(
                        group -> group == null ? null : group.displayName(),
                        CourseGroup::fromDisplayName)
                .bind(Course::getCourseGroup, Course::setCourseGroup);
        groupCol.setEditorComponent(groupField);

        NumberField creditsField = new NumberField();
        creditsField.setStep(0.5);
        creditsField.setMin(0);
        creditsField.setWidthFull();
        binder.forField(creditsField)
                .withConverter(
                        value -> value == null ? null : value.floatValue(),
                        value -> value == null ? null : value.doubleValue())
                .bind(Course::getCredits, Course::setCredits);
        creditsCol.setEditorComponent(creditsField);

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
        // editor is attached to, which immediately closes the editor again - that
        // was making the Edit and New buttons appear to do nothing.
    }

    /** Per-row "Edit"/"Delete" buttons shown when the row is not being edited. */
    private HorizontalLayout buildRowActions(Course course) {
        Button edit = new Button("Edit", e -> editItem(course));

        Button delete = new Button("Delete", e -> delete(course));
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        delete.setEnabled(course.getId() != null);

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

    private String courseGroupLabel(Course course) {
        // The stored value is already the display name (e.g. "Baskurser"), so
        // show it directly. Fall back to empty for null.
        return course.getCourseGroup() == null ? "" : course.getCourseGroup();
    }

    /**
     * Opens the buffered editor on the given row. Only one row can be edited at
     * a time, so any edit already in progress is cancelled first (which also
     * drops a not-yet-saved new row via the cancel listener).
     */
    private void editItem(Course course) {
        if (editor.isOpen()) {
            editor.cancel();
        }
        editor.editItem(course);
    }

    private void addCourse() {
        // Cancel any edit in progress first (this also discards a previous
        // pending-new row via the cancel listener) before adding the new one.
        if (editor.isOpen()) {
            editor.cancel();
        }
        Course fresh = new Course();
        // Add the row so the editor has something to attach to, then open it.
        courses.add(0, fresh);
        grid.getListDataView().refreshAll();
        pendingNew = fresh;
        editor.editItem(fresh);
    }

    private void loadCourses() {
        try {
            courses.clear();
            courses.addAll(courseService.getAllCourses());
            grid.setItems(courses);
        } catch (Exception ex) {
            log.error("Failed to load courses", ex);
            notifyError("Could not load courses: " + ex.getMessage());
        }
    }

    /**
     * Commits the buffered editor and persists the edited course.
     *
     * <p>{@link Editor#save()} runs the binder validation and, only if valid,
     * writes the field values into the edited bean. We then persist that bean
     * and refresh the grid. Mirrors POST/PUT /rest/courses (create/update).
     */
    @PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    private void save() {
        Course edited = editor.getItem();
        if (edited == null) {
            return;
        }
        log.info("[save] before editor.save(): courseGroup={}", edited.getCourseGroup());
        // save() returns false and keeps the editor open when validation fails.
        if (!editor.save()) {
            notifyError("Please fix the highlighted fields.");
            return;
        }
        try {
            Course saved = courseService.saveCourse(edited);
            // This row is now persisted, so it is no longer a pending-new row.
            pendingNew = null;
            notifySuccess("Course saved: " + saved.getCode());
            loadCourses();
        } catch (Exception ex) {
            log.error("Failed to save course", ex);
            notifyError("Could not save course: " + ex.getMessage());
        }
    }

    /** Mirrors DELETE /rest/courses/{id}. */
    @PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    private void delete(Course course) {
        if (course == null || course.getId() == null) {
            return;
        }
        try {
            courseService.deleteCourse(course.getId());
            notifySuccess("Course deleted.");
            loadCourses();
        } catch (Exception ex) {
            log.error("Failed to delete course", ex);
            notifyError("Could not delete course: " + ex.getMessage());
        }
    }

    /** Removes an added-but-never-saved row when its edit is cancelled. */
    private void discardPendingNew() {
        if (pendingNew != null) {
            courses.remove(pendingNew);
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
}
