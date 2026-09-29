package se.uu.ebc.bemanning.ui;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import se.uu.ebc.bemanning.entity.assignment.Assignment;
import se.uu.ebc.bemanning.entity.assignment.AssignmentOutcome;
import se.uu.ebc.bemanning.entity.assignment.AssignmentPlan;
import se.uu.ebc.bemanning.entity.assignment.AssignmentTE;
import se.uu.ebc.bemanning.entity.OrganisationUnit;
import se.uu.ebc.bemanning.entity.assignment.CourseStaffing;
import se.uu.ebc.bemanning.entity.assignment.CourseStaffingModern;
import se.uu.ebc.bemanning.entity.courseinstance.CourseInstance;
import se.uu.ebc.bemanning.entity.staff.Staff;
import se.uu.ebc.bemanning.service.CourseService;
import se.uu.ebc.bemanning.service.CourseStaffingService;
import se.uu.ebc.bemanning.service.OrganisationUnitService;
import se.uu.ebc.bemanning.service.StaffService;

import com.vaadin.flow.shared.Registration;

import lombok.extern.slf4j.Slf4j;

/**
 * Server-side Vaadin view for editing {@link CourseStaffing} entities with an
 * attached editor form.
 *
 * <p>Layout is a master/detail: a {@link Grid} on the left lists every
 * {@code CourseStaffing} (showing only the properties declared on the
 * {@code CourseStaffing} base class), and a form on the right edits the three
 * assignments ({@link AssignmentPlan}, {@link AssignmentTE},
 * {@link AssignmentOutcome}) of the selected staffing.
 *
 * <p>Only {@link CourseStaffingModern} rows are editable - those are the ones
 * that own the plan/te/outcome assignments. {@code CourseStaffingLegacy} rows
 * are shown for reference but selecting one only displays a read-only notice;
 * the form stays disabled. Adding a new row always creates a
 * {@code CourseStaffingModern}.
 *
 * <p>Data access goes through {@link CourseStaffingService}.
 */
@Route(value = "assignments", layout = MainLayout.class)
@PageTitle("Assignments")
@Menu(order = 0, icon = "icons/hourglass.svg", title = "Bemanning")
@AnonymousAllowed // Vaadin navigation access control requires an explicit access
                  // annotation. Write actions remain guarded by @PreAuthorize.
@Slf4j
public class AssignmentView extends VerticalLayout {

    private final CourseStaffingService courseStaffingService;
    // Staff selector is backed by StaffService, course instances by CourseService,
    // and departments by OrganisationUnitService (all returning entities).
    private final StaffService staffService;
    private final CourseService courseService;
    private final OrganisationUnitService organisationUnitService;
    private final YearContext yearContext;

    private final Grid<CourseStaffing> grid = new Grid<>(CourseStaffing.class, false);
    private final List<CourseStaffing> staffings = new ArrayList<>();

    // Binder for the CourseStaffing-level fields (staff, course instance,
    // assigning dept, note).
    private final Binder<CourseStaffing> staffingBinder = new Binder<>(CourseStaffing.class);
    // One binder per editable assignment of the selected modern staffing.
    private final Binder<Assignment> planBinder = new Binder<>(Assignment.class);
    private final Binder<Assignment> teBinder = new Binder<>(Assignment.class);
    private final Binder<Assignment> outcomeBinder = new Binder<>(Assignment.class);

    // CourseStaffing-level editors.
    private final ComboBox<Staff> staffField = new ComboBox<>("Personal");
    private final ComboBox<CourseInstance> courseInstanceField = new ComboBox<>("Kurstillfälle");
    private final ComboBox<OrganisationUnit> assigningDeptField = new ComboBox<>("Bemannande enhet");
    // Top-level note lives on CourseStaffing itself.
    private final TextField staffingNote = new TextField("Anteckningar");

    private final H3 formTitle = new H3("Bemanning");
    private final Span legacyNotice = new Span(
            "Legacy staffing rows are read-only and cannot be edited here.");
    // The aligned hours grid (Plan/TE/Outcome rows) and the per-section notes.
    private FormLayout hoursGrid;
    private FormLayout notesForm;
    private final VerticalLayout formPanel = new VerticalLayout();

    private final Button saveButton = new Button("Save");
    private final Button deleteButton = new Button("Delete");
    private final Button newButton = new Button("New staffing");

    // The staffing currently shown in the form, or null when nothing editable
    // is selected.
    private CourseStaffingModern current;

    public AssignmentView(CourseStaffingService courseStaffingService,
                          StaffService staffService,
                          CourseService courseService,
                          OrganisationUnitService organisationUnitService,
                          YearContext yearContext) {
        this.courseStaffingService = courseStaffingService;
        this.staffService = staffService;
        this.courseService = courseService;
        this.organisationUnitService = organisationUnitService;
        this.yearContext = yearContext;
        setSizeFull();

        configureGrid();
        buildFormPanel();
        loadSelectorItems();
        add(buildToolbar(), buildBody());
        loadStaffings();
        showStaffing(null);

        // Reload for the selected year whenever it changes; unregister on detach
        // so the session-scoped YearContext does not retain a detached view.
        addAttachListener(attach -> {
            Registration reg = yearContext.addYearChangeListener(year -> {
                loadStaffings();
                showStaffing(null);
            });
            addDetachListener(detach -> reg.remove());
        });
    }

    /** Populates the staff / course-instance / department selectors. */
    private void loadSelectorItems() {
        try {
            staffField.setItems(staffService.getStaffByYear(yearContext.getYear()));
            staffField.setItemLabelGenerator(this::staffOptionLabel);
        } catch (Exception ex) {
            log.error("Failed to load staff", ex);
        }
        try {
            courseInstanceField.setItems(courseService.getAllCourseInstances());
            courseInstanceField.setItemLabelGenerator(
                    ci -> ci == null ? "" : ci.getDesignation());
        } catch (Exception ex) {
            log.error("Failed to load course instances", ex);
        }
        try {
            assigningDeptField.setItems(organisationUnitService.getAllOrganisationUnits());
            assigningDeptField.setItemLabelGenerator(
                    ou -> ou == null ? "" : ou.getAbbreviation());
        } catch (Exception ex) {
            log.error("Failed to load organisation units", ex);
        }
    }

    private String staffOptionLabel(Staff staff) {
        if (staff == null || staff.getPerson() == null) {
            return "";
        }
        String year = staff.getYear() == null ? "" : " (" + staff.getYear() + ")";
        return staff.getPerson().getName() + year;
    }

    private HorizontalLayout buildToolbar() {
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newButton.addClickListener(e -> addStaffing());
        return new HorizontalLayout(newButton);
    }

    private VerticalLayout buildBody() {
        // Grid on top, editor form below it (stacked vertically), each taking
        // half of the available height.
        VerticalLayout body = new VerticalLayout(grid, formPanel);
        // flex-basis:0 + equal grow makes the two panels split the height 50/50
        // regardless of their content size.
        grid.getStyle().set("flex-basis", "0");
        // min-height:0 lets the grid shrink within its flex track instead of
        // being pinned to its content/100% height, so the 50/50 split holds.
        grid.getStyle().set("min-height", "0");
        formPanel.getStyle().set("flex-basis", "0");
        formPanel.getStyle().set("min-height", "0");
        body.setFlexGrow(1, grid);
        body.setFlexGrow(1, formPanel);
        // Let the form panel scroll internally instead of pushing past its half.
        formPanel.getStyle().set("overflow", "auto");
        body.setPadding(true);
        body.setSizeFull();
        return body;
    }

    /** Grid columns correspond only to properties of the CourseStaffing base. */
    private void configureGrid() {
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();

        grid.addColumn(this::staffLabel).setHeader("Personal").setSortable(true).setAutoWidth(true);
        grid.addColumn(this::courseInstanceLabel).setHeader("Kurstillfälle")
                .setSortable(true).setAutoWidth(true);
        grid.addColumn(this::assigningDeptLabel).setHeader("Bemannande").setWidth("80px").setFlexGrow(0);
        grid.addColumn(cs -> cs.isLegacy() ? "Legacy" : "Modern").setHeader("Typ").setWidth("100px").setFlexGrow(0);
        grid.addColumn(cs -> safeFloat(cs::getTotalHours)).setHeader("Timmar totalt").setWidth("100px").setFlexGrow(0);
        grid.addColumn(cs -> safeFloat(cs::getPlainTeachingHours))
                .setHeader("Undervisningstimmar").setWidth("100px").setFlexGrow(0);
        grid.addColumn(CourseStaffing::getNote).setHeader("Anteckningar").setAutoWidth(true);

        grid.asSingleSelect().addValueChangeListener(e -> onSelect(e.getValue()));
    }

    private void onSelect(CourseStaffing selected) {
        if (selected instanceof CourseStaffingModern modern) {
            showStaffing(modern);
        } else {
            // Legacy or null: nothing editable.
            showStaffing(null);
            legacyNotice.setVisible(selected != null && selected.isLegacy());
        }
    }

    private String staffLabel(CourseStaffing cs) {
        Staff staff = cs.getStaff();
        if (staff == null || staff.getPerson() == null) {
            return "";
        }
        return staff.getPerson().getName();
    }

    private String courseInstanceLabel(CourseStaffing cs) {
        CourseInstance ci = cs.getCourseInstance();
        return ci == null ? "" : ci.getDesignation();
    }

    private String assigningDeptLabel(CourseStaffing cs) {
        return cs.getAssigningDept() == null ? "" : cs.getAssigningDept().getAbbreviation();
    }

    /** Guards the computed-hours getters, which dereference the assignments. */
    private String safeFloat(FloatSupplier supplier) {
        try {
            return String.valueOf(supplier.getAsFloat());
        } catch (Exception ex) {
            return "";
        }
    }

    @FunctionalInterface
    private interface FloatSupplier {
        float getAsFloat();
    }

    /* ---- Form ---- */

    private void buildFormPanel() {
        staffField.setWidthFull();
        courseInstanceField.setWidthFull();
        assigningDeptField.setWidthFull();
        staffingNote.setWidthFull();

        // CourseStaffing-level bindings. staff/courseInstance/assigningDept are
        // @NotNull on the entity, so require them here.
        staffingBinder.forField(staffField)
                .asRequired("Staff is required")
                .bind(CourseStaffing::getStaff, CourseStaffing::setStaff);
        staffingBinder.forField(courseInstanceField)
                .asRequired("Course instance is required")
                .bind(CourseStaffing::getCourseInstance, CourseStaffing::setCourseInstance);
        staffingBinder.forField(assigningDeptField)
                .asRequired("Assigning department is required")
                .bind(CourseStaffing::getAssigningDept, CourseStaffing::setAssigningDept);
        staffingBinder.forField(staffingNote)
                .bind(CourseStaffing::getNote, CourseStaffing::setNote);

        FormLayout staffingForm = new FormLayout();
        staffingForm.add(staffField, courseInstanceField, assigningDeptField, staffingNote);

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> save());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> delete());
        HorizontalLayout actions = new HorizontalLayout(saveButton, deleteButton);

        hoursGrid = buildHoursGrid();
        notesForm = buildNotesForm();

        formPanel.setPadding(false);
        formPanel.add(
//                formTitle,
//                legacyNotice,
//                staffingForm,
                new H3("Timmar"),
                hoursGrid,
//                new H3("Anteckningar"),
//                notesForm,
                actions);
        formPanel.setWidthFull();
    }

    // Column headers for the six hour categories, in field order.
    private static final String[] HOUR_HEADERS =
            {"Admin", "Utveckling", "Föreläsning", "Lab", "Exkursion", "Seminarium"};

    /**
     * Builds a single 7-column grid where each of Plan / TE / Outcome occupies one
     * row: a leading row-label followed by the six hour fields. Because all rows
     * share the same 7-column layout, the fields line up column-wise (each
     * category sits in the same column across the three sections). Note fields are
     * excluded here (see {@link #buildNotesForm()}).
     */
    private FormLayout buildHoursGrid() {
        FormLayout grid = new FormLayout();
        // Fixed 7 columns at any width so the rows stay aligned as a table.
        grid.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 7));
        grid.setWidthFull();

        // Header row: empty corner cell + the six category labels.
        grid.add(columnHeader(""));
        for (String header : HOUR_HEADERS) {
            grid.add(columnHeader(header));
        }

        // One row per assignment; the row label goes in the first column.
        addHoursRow(grid, "Planerad", planBinder);
        addHoursRow(grid, "TimeEdit (TE)", teBinder);
        addHoursRow(grid, "Utfall", outcomeBinder);

        return grid;
    }

    private Span columnHeader(String text) {
        Span span = new Span(text);
        span.getStyle().set("font-weight", "600");
        return span;
    }

    /**
     * Adds one section's row to the shared hours grid: a label cell followed by
     * the six hour fields (each bound to {@code binder}). The field labels are
     * suppressed since the column headers label them.
     */
    private void addHoursRow(FormLayout grid, String label, Binder<Assignment> binder) {
        Span rowLabel = new Span(label);
        rowLabel.getStyle().set("font-weight", "600");
        grid.add(rowLabel);

        grid.add(hoursField(binder, Assignment::getHoursAdmin, Assignment::setHoursAdmin));
        grid.add(hoursField(binder, Assignment::getHoursDevelopment, Assignment::setHoursDevelopment));
        grid.add(hoursField(binder, Assignment::getHoursLecture, Assignment::setHoursLecture));
        grid.add(hoursField(binder, Assignment::getHoursPractical, Assignment::setHoursPractical));
        grid.add(hoursField(binder, Assignment::getHoursExcursion, Assignment::setHoursExcursion));
        grid.add(hoursField(binder, Assignment::getHoursSeminar, Assignment::setHoursSeminar));
    }

    /** A label-less hours NumberField bound to one Float property. */
    private NumberField hoursField(Binder<Assignment> binder,
                                   com.vaadin.flow.function.ValueProvider<Assignment, Float> getter,
                                   com.vaadin.flow.data.binder.Setter<Assignment, Float> setter) {
        NumberField field = new NumberField();
        field.setStep(0.5);
        field.setMin(0);
        field.setWidthFull();
        binder.forField(field).withConverter(toFloat(), toDouble()).bind(getter, setter);
        return field;
    }

    /** Per-section note fields, laid out one per line (excluded from the hours row). */
    private FormLayout buildNotesForm() {
        FormLayout notes = new FormLayout();
        notes.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        notes.setWidthFull();
        notes.add(noteField("Planerad - anteckning", planBinder));
        notes.add(noteField("TimeEdit (TE) - anteckning", teBinder));
        notes.add(noteField("Utfall - anteckning", outcomeBinder));
        return notes;
    }

    private TextField noteField(String label, Binder<Assignment> binder) {
        TextField note = new TextField(label);
        note.setWidthFull();
        binder.forField(note).bind(Assignment::getNote, Assignment::setNote);
        return note;
    }

    /** Presentation (Double from NumberField) -> model (Float on Assignment). */
    private static com.vaadin.flow.function.SerializableFunction<Double, Float> toFloat() {
        return value -> value == null ? null : value.floatValue();
    }

    /** Model (Float on Assignment) -> presentation (Double for NumberField). */
    private static com.vaadin.flow.function.SerializableFunction<Float, Double> toDouble() {
        return value -> value == null ? null : value.doubleValue();
    }

    /* ---- Data / actions ---- */

    private void addStaffing() {
        CourseStaffingModern fresh = new CourseStaffingModern();
        // Every modern staffing owns a plan; te/outcome are created on demand.
        fresh.setPlan(newAssignment(new AssignmentPlan(), fresh));
        fresh.setTe((AssignmentTE) newAssignment(new AssignmentTE(), fresh));
        fresh.setOutcome((AssignmentOutcome) newAssignment(new AssignmentOutcome(), fresh));
        staffings.add(0, fresh);
        grid.getListDataView().refreshAll();
        grid.select(fresh);
        showStaffing(fresh);
    }

    /** Initializes an assignment's hours to zero and links it back to the staffing. */
    private <A extends Assignment> A newAssignment(A assignment, CourseStaffing owner) {
        assignment.setHoursAdmin(0.0f);
        assignment.setHoursDevelopment(0.0f);
        assignment.setHoursLecture(0.0f);
        assignment.setHoursPractical(0.0f);
        assignment.setHoursExcursion(0.0f);
        assignment.setHoursSeminar(0.0f);
        assignment.setCourseStaffing(owner);
        return assignment;
    }

    private void loadStaffings() {
        try {
            String year = yearContext.getYear();
            staffings.clear();
            // Year-scoped query (year filtering happens in the query layer).
            staffings.addAll(courseStaffingService.getCourseStaffingsByYear(year));
            grid.setItems(staffings);
        } catch (Exception ex) {
            log.error("Failed to load course staffings", ex);
            notifyError("Could not load staffings: " + ex.getMessage());
        }
    }

    /**
     * Binds the form to the given modern staffing, or clears/disables it when
     * {@code modern} is null. Ensures plan/te/outcome exist so the binders have
     * a bean to read/write.
     */
    private void showStaffing(CourseStaffingModern modern) {
        this.current = modern;
        legacyNotice.setVisible(false);
        boolean editing = modern != null;
        setFormEnabled(editing);

        if (!editing) {
            staffingBinder.readBean(null);
            planBinder.readBean(null);
            teBinder.readBean(null);
            outcomeBinder.readBean(null);
            return;
        }

        // Ensure the three assignments exist before binding.
        if (modern.getPlan() == null) {
            modern.setPlan(newAssignment(new AssignmentPlan(), modern));
        }
        if (modern.getTe() == null) {
            modern.setTe((AssignmentTE) newAssignment(new AssignmentTE(), modern));
        }
        if (modern.getOutcome() == null) {
            modern.setOutcome((AssignmentOutcome) newAssignment(new AssignmentOutcome(), modern));
        }

        staffingBinder.readBean(modern);
        planBinder.readBean(modern.getPlan());
        teBinder.readBean(modern.getTe());
        outcomeBinder.readBean(modern.getOutcome());
    }

    private void setFormEnabled(boolean enabled) {
        staffField.setEnabled(enabled);
        courseInstanceField.setEnabled(enabled);
        assigningDeptField.setEnabled(enabled);
        staffingNote.setEnabled(enabled);
        hoursGrid.setEnabled(enabled);
        notesForm.setEnabled(enabled);
        saveButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled && current != null && current.getId() != null);
        formTitle.setText(enabled ? "Edit assignment" : "Assignment");
    }

    @PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    private void save() {
        if (current == null) {
            return;
        }
        try {
            // Write the staffing-level fields (staff, course instance, dept,
            // note) and the three assignment forms into their beans. writeBean
            // throws ValidationException if a required selector is empty.
            staffingBinder.writeBean(current);
            planBinder.writeBean(current.getPlan());
            teBinder.writeBean(current.getTe());
            outcomeBinder.writeBean(current.getOutcome());

            CourseStaffing saved = courseStaffingService.saveCourseStaffing(current);
            notifySuccess("Staffing saved.");
            loadStaffings();
            grid.select(saved);
        } catch (ValidationException ve) {
            notifyError("Please fix the highlighted fields.");
        } catch (Exception ex) {
            log.error("Failed to save staffing", ex);
            notifyError("Could not save staffing: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    private void delete() {
        if (current == null || current.getId() == null) {
            return;
        }
        try {
            courseStaffingService.deleteCourseStaffing(current.getId());
            notifySuccess("Staffing deleted.");
            loadStaffings();
            showStaffing(null);
        } catch (Exception ex) {
            log.error("Failed to delete staffing", ex);
            notifyError("Could not delete staffing: " + ex.getMessage());
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
