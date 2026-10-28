package se.uu.ebc.bemanning.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import se.uu.ebc.bemanning.dto.YearlyStaffingDTO;
import se.uu.ebc.bemanning.entity.FactorRecord;
import se.uu.ebc.bemanning.service.YearlyStaffingService;

import lombok.extern.slf4j.Slf4j;

/**
 * Server-side Vaadin view for editing {@link YearlyStaffingDTO} rows with a
 * buffered inline grid editor, backed by {@link YearlyStaffingService}.
 *
 * <p>The service works in DTOs, so the grid binds to {@link YearlyStaffingDTO}.
 * Editable columns: department, year and the four teaching factors (held in the
 * immutable {@link FactorRecord}). The course-instance count is derived and
 * shown read-only.
 */
@Route(value = "yearlystaffing", layout = MainLayout.class)
@PageTitle("Årlig bemanning")
@Menu(order = 8, icon = "icons/calendar-o.svg", title = "Årlig bemanning")
@AnonymousAllowed // Vaadin navigation access control requires an explicit access
                  // annotation. Write actions remain guarded by @PreAuthorize.
@Slf4j
public class YearlyStaffingView extends VerticalLayout {

    private final YearlyStaffingService yearlyStaffingService;

    private final Grid<YearlyStaffingDTO> grid = new Grid<>(YearlyStaffingDTO.class, false);
    private final Binder<YearlyStaffingDTO> binder = new Binder<>(YearlyStaffingDTO.class);
    private final Editor<YearlyStaffingDTO> editor = grid.getEditor();

    private final List<YearlyStaffingDTO> rows = new ArrayList<>();

    private final Button newButton = new Button("Ny årsbemanning");

    private YearlyStaffingDTO pendingNew;

    public YearlyStaffingView(YearlyStaffingService yearlyStaffingService) {
        this.yearlyStaffingService = yearlyStaffingService;
        setSizeFull();

        configureGrid();
        configureEditor();
        add(buildToolbar(), grid);
        loadRows();
    }

    private HorizontalLayout buildToolbar() {
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newButton.addClickListener(e -> addRow());
        return new HorizontalLayout(newButton);
    }

    private void configureGrid() {
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();

        Grid.Column<YearlyStaffingDTO> deptCol = grid.addColumn(YearlyStaffingDTO::getDept)
                .setHeader("Enhet").setSortable(true).setAutoWidth(true);
        Grid.Column<YearlyStaffingDTO> yearCol = grid.addColumn(YearlyStaffingDTO::getYear)
                .setHeader("År").setSortable(true).setAutoWidth(true);
        Grid.Column<YearlyStaffingDTO> excursionCol = grid.addColumn(dto -> factor(dto, FactorRecord::EXCURSION_FACTOR))
                .setHeader("Exkursion").setAutoWidth(true);
        Grid.Column<YearlyStaffingDTO> practicalCol = grid.addColumn(dto -> factor(dto, FactorRecord::PRACTICAL_FACTOR))
                .setHeader("Lab").setAutoWidth(true);
        Grid.Column<YearlyStaffingDTO> professorCol = grid.addColumn(dto -> factor(dto, FactorRecord::PROFESSOR_LECTURE_FACTOR))
                .setHeader("Föreläsning (lärare)").setAutoWidth(true);
        Grid.Column<YearlyStaffingDTO> studentCol = grid.addColumn(dto -> factor(dto, FactorRecord::STUDENT_LECTURE_FACTOR))
                .setHeader("Föreläsning (student)").setAutoWidth(true);
        // Derived, read-only.
        grid.addColumn(YearlyStaffingDTO::getNumberOfCourseInstances)
                .setHeader("Antal kurstillfällen").setAutoWidth(true);

        Grid.Column<YearlyStaffingDTO> actionsCol = grid.addComponentColumn(this::buildRowActions)
                .setHeader("Handling").setAutoWidth(true).setFlexGrow(0);

        buildEditorComponents(deptCol, yearCol, excursionCol, practicalCol,
                professorCol, studentCol, actionsCol);
    }

    private void buildEditorComponents(Grid.Column<YearlyStaffingDTO> deptCol,
                                       Grid.Column<YearlyStaffingDTO> yearCol,
                                       Grid.Column<YearlyStaffingDTO> excursionCol,
                                       Grid.Column<YearlyStaffingDTO> practicalCol,
                                       Grid.Column<YearlyStaffingDTO> professorCol,
                                       Grid.Column<YearlyStaffingDTO> studentCol,
                                       Grid.Column<YearlyStaffingDTO> actionsCol) {
        TextField deptField = new TextField();
        deptField.setWidthFull();
        binder.forField(deptField).bind(YearlyStaffingDTO::getDept, YearlyStaffingDTO::setDept);
        deptCol.setEditorComponent(deptField);

        TextField yearField = new TextField();
        yearField.setWidthFull();
        binder.forField(yearField)
                .asRequired("År krävs")
                .bind(YearlyStaffingDTO::getYear, YearlyStaffingDTO::setYear);
        yearCol.setEditorComponent(yearField);

        // The four factors live in the immutable FactorRecord. Each editor reads
        // its factor from the record and, on write, rebuilds the record with that
        // one factor replaced (see withFactor()).
        excursionCol.setEditorComponent(factorField(FactorRecord::EXCURSION_FACTOR,
                (rec, v) -> new FactorRecord(v, rec.PRACTICAL_FACTOR(), rec.PROFESSOR_LECTURE_FACTOR(), rec.STUDENT_LECTURE_FACTOR())));
        practicalCol.setEditorComponent(factorField(FactorRecord::PRACTICAL_FACTOR,
                (rec, v) -> new FactorRecord(rec.EXCURSION_FACTOR(), v, rec.PROFESSOR_LECTURE_FACTOR(), rec.STUDENT_LECTURE_FACTOR())));
        professorCol.setEditorComponent(factorField(FactorRecord::PROFESSOR_LECTURE_FACTOR,
                (rec, v) -> new FactorRecord(rec.EXCURSION_FACTOR(), rec.PRACTICAL_FACTOR(), v, rec.STUDENT_LECTURE_FACTOR())));
        studentCol.setEditorComponent(factorField(FactorRecord::STUDENT_LECTURE_FACTOR,
                (rec, v) -> new FactorRecord(rec.EXCURSION_FACTOR(), rec.PRACTICAL_FACTOR(), rec.PROFESSOR_LECTURE_FACTOR(), v)));

        actionsCol.setEditorComponent(buildEditorActions());
    }

    /**
     * Builds a NumberField bound to one factor of the DTO's FactorRecord. The
     * getter reads the factor; the setter rebuilds the record via {@code rebuild}
     * (records are immutable). A null record defaults to all-zero factors.
     */
    private NumberField factorField(Function<FactorRecord, Float> getter,
                                    FactorRebuild rebuild) {
        NumberField field = new NumberField();
        field.setStep(0.5);
        field.setWidthFull();
        binder.forField(field)
                .withConverter(toFloat(), toDouble())
                .bind(
                        dto -> getter.apply(recordOf(dto)),
                        (dto, value) -> dto.setTeachingFactors(
                                rebuild.apply(recordOf(dto), value == null ? 0f : value)));
        return field;
    }

    /** Rebuilds a FactorRecord with one factor replaced. */
    @FunctionalInterface
    private interface FactorRebuild {
        FactorRecord apply(FactorRecord current, float value);
    }

    private void configureEditor() {
        editor.setBinder(binder);
        editor.setBuffered(true);
        editor.addCancelListener(e -> discardPendingNew());
    }

    private HorizontalLayout buildRowActions(YearlyStaffingDTO dto) {
        Button edit = new Button("Ändra", e -> editItem(dto));

        Button delete = new Button("Ta bort", e -> delete(dto));
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        delete.setEnabled(dto.getId() != null);

        HorizontalLayout actions = new HorizontalLayout(edit, delete);
        actions.setPadding(false);
        return actions;
    }

    private HorizontalLayout buildEditorActions() {
        Button save = new Button("Spara", e -> save());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);

        Button cancel = new Button("Avbryt", e -> editor.cancel());

        HorizontalLayout actions = new HorizontalLayout(save, cancel);
        actions.setPadding(false);
        return actions;
    }

    /* ---- Helpers ---- */

    /** The DTO's FactorRecord, or an all-zero record if none is set. */
    private FactorRecord recordOf(YearlyStaffingDTO dto) {
        FactorRecord rec = dto.getTeachingFactors();
        return rec == null ? new FactorRecord(0f, 0f, 0f, 0f) : rec;
    }

    /** A factor value for display, or empty when no record is set. */
    private String factor(YearlyStaffingDTO dto, Function<FactorRecord, Float> getter) {
        FactorRecord rec = dto.getTeachingFactors();
        return rec == null ? "" : String.valueOf(getter.apply(rec));
    }

    private void editItem(YearlyStaffingDTO dto) {
        if (editor.isOpen()) {
            editor.cancel();
        }
        editor.editItem(dto);
    }

    private void addRow() {
        if (editor.isOpen()) {
            editor.cancel();
        }
        YearlyStaffingDTO fresh = new YearlyStaffingDTO();
        fresh.setTeachingFactors(new FactorRecord(0f, 0f, 0f, 0f));
        rows.add(0, fresh);
        grid.getListDataView().refreshAll();
        pendingNew = fresh;
        editor.editItem(fresh);
    }

    private void loadRows() {
        try {
            rows.clear();
            rows.addAll(yearlyStaffingService.getAllYearlyStaffings());
            grid.setItems(rows);
        } catch (Exception ex) {
            log.error("Failed to load yearly staffings", ex);
            notifyError("Kunde inte ladda årsbemanningar: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    private void save() {
        YearlyStaffingDTO edited = editor.getItem();
        if (edited == null) {
            return;
        }
        if (!editor.save()) {
            notifyError("Rätta de markerade fälten.");
            return;
        }
        try {
            yearlyStaffingService.saveYearlyStaffing(edited);
            pendingNew = null;
            notifySuccess("Årsbemanning sparad.");
            loadRows();
        } catch (Exception ex) {
            log.error("Failed to save yearly staffing", ex);
            notifyError("Kunde inte spara: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    private void delete(YearlyStaffingDTO dto) {
        if (dto == null || dto.getId() == null) {
            return;
        }
        try {
            yearlyStaffingService.deleteYearlyStaffing(dto.getId());
            notifySuccess("Årsbemanning borttagen.");
            loadRows();
        } catch (Exception ex) {
            log.error("Failed to delete yearly staffing", ex);
            notifyError("Kunde inte ta bort: " + ex.getMessage());
        }
    }

    private void discardPendingNew() {
        if (pendingNew != null) {
            rows.remove(pendingNew);
            pendingNew = null;
            grid.getListDataView().refreshAll();
        }
    }

    private static SerializableFunction<Double, Float> toFloat() {
        return value -> value == null ? null : value.floatValue();
    }

    private static SerializableFunction<Float, Double> toDouble() {
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
}
