package se.uu.ebc.bemanning.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import se.uu.ebc.bemanning.entity.OrganisationUnit;
import se.uu.ebc.bemanning.entity.YearsOfHierarchy;
import se.uu.ebc.bemanning.service.OrganisationUnitService;

import lombok.extern.slf4j.Slf4j;

/**
 * Server-side Vaadin view for editing {@link OrganisationUnit} entities with a
 * buffered inline grid editor, plus a nested inline-editable grid for each
 * unit's {@link YearsOfHierarchy} sub-unit relationships (the rows where this
 * unit is the super-unit).
 *
 * <p>The master grid lists organisation units; expanding a row (item-details)
 * reveals a nested grid of that unit's hierarchy entries (its sub-units and the
 * year span they are valid for), itself inline-editable. Both grids use Vaadin's
 * <em>buffered</em> editor, so edits are only written back and persisted on
 * <em>Save</em>.
 *
 * <p>Data access goes through {@link OrganisationUnitService}; the sub-unit
 * selector is backed by the same service's unit list.
 */
@Route(value = "organisationunits", layout = MainLayout.class)
@PageTitle("Organisation units")
@Menu(order = 9, icon = "icons/sitemap.svg", title = "Organisation")
@AnonymousAllowed // Vaadin navigation access control requires an explicit access
                  // annotation. Write actions remain guarded by @PreAuthorize.
@Slf4j
public class OrganisationUnitView extends VerticalLayout {

    private final OrganisationUnitService organisationUnitService;

    private final Grid<OrganisationUnit> grid = new Grid<>(OrganisationUnit.class, false);
    private final Binder<OrganisationUnit> binder = new Binder<>(OrganisationUnit.class);
    private final Editor<OrganisationUnit> editor = grid.getEditor();

    private final List<OrganisationUnit> units = new ArrayList<>();
    // In-memory list data view captured from grid.setItems(...); used to apply
    // the per-column filters.
    private GridListDataView<OrganisationUnit> dataView;

    // "All" sentinel for the three-state boolean filters.
    private static final String FILTER_ALL = "(alla)";
    // Holds the current per-column filter criteria (AND-ed).
    private final OuFilter filter = new OuFilter();

    private final Button newButton = new Button("Ny enhet");

    // Tracks a row added via "Ny enhet" but not yet persisted, so a cancelled
    // edit removes it instead of leaving an orphaned blank row.
    private OrganisationUnit pendingNew;

    public OrganisationUnitView(OrganisationUnitService organisationUnitService) {
        this.organisationUnitService = organisationUnitService;
        setSizeFull();

        configureGrid();
        configureEditor();
        add(buildToolbar(), grid);
        loadUnits();
    }

    private HorizontalLayout buildToolbar() {
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newButton.addClickListener(e -> addUnit());
        return new HorizontalLayout(newButton);
    }

    /* ---- Master grid (OrganisationUnit) ---- */

    private void configureGrid() {
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();

        Grid.Column<OrganisationUnit> svNameCol = grid.addColumn(OrganisationUnit::getSvName)
                .setHeader("Namn (sv)").setSortable(true).setAutoWidth(true);
        Grid.Column<OrganisationUnit> enNameCol = grid.addColumn(OrganisationUnit::getEnName)
                .setHeader("Namn (en)").setSortable(true).setAutoWidth(true);
        Grid.Column<OrganisationUnit> abbrevCol = grid.addColumn(OrganisationUnit::getAbbreviation)
                .setHeader("Förkortning").setSortable(true).setAutoWidth(true);
        Grid.Column<OrganisationUnit> kindCol = grid.addColumn(OrganisationUnit::getUnitKind)
                .setHeader("Typ").setSortable(true).setAutoWidth(true);
        Grid.Column<OrganisationUnit> inSystemCol = grid.addColumn(ou -> boolLabel(ou.getInSystem()))
                .setHeader("I systemet").setAutoWidth(true);
        Grid.Column<OrganisationUnit> legacyCol = grid.addColumn(ou -> boolLabel(ou.getLegacyUnit()))
                .setHeader("Legacy").setAutoWidth(true);
        Grid.Column<OrganisationUnit> ecoCol = grid.addColumn(ou -> boolLabel(ou.getCourseEconomyHolder()))
                .setHeader("Ekonomihållare").setAutoWidth(true);

        Grid.Column<OrganisationUnit> actionsCol = grid.addComponentColumn(this::buildRowActions)
                .setHeader("Handling").setAutoWidth(true).setFlexGrow(0);

        buildEditorComponents(svNameCol, enNameCol, abbrevCol, kindCol,
                inSystemCol, legacyCol, ecoCol, actionsCol);

        buildFilterRow(svNameCol, enNameCol, abbrevCol, kindCol,
                inSystemCol, legacyCol, ecoCol);

        // Nested grid: each master row expands to its YearsOfHierarchy sub-units.
        grid.setItemDetailsRenderer(new ComponentRenderer<>(this::buildHierarchyPanel));
    }

    /**
     * Adds a header filter row covering every data column: text filters for the
     * name/abbreviation/type columns and three-state (Alla/Ja/Nej) selects for
     * the boolean flag columns. All active filters are AND-ed; text matches are
     * case-insensitive substrings.
     */
    private void buildFilterRow(Grid.Column<OrganisationUnit> svNameCol,
                                Grid.Column<OrganisationUnit> enNameCol,
                                Grid.Column<OrganisationUnit> abbrevCol,
                                Grid.Column<OrganisationUnit> kindCol,
                                Grid.Column<OrganisationUnit> inSystemCol,
                                Grid.Column<OrganisationUnit> legacyCol,
                                Grid.Column<OrganisationUnit> ecoCol) {
        HeaderRow filterRow = grid.appendHeaderRow();
        filterRow.getCell(svNameCol).setComponent(
                textFilter("Namn (sv)", value -> filter.svName = value));
        filterRow.getCell(enNameCol).setComponent(
                textFilter("Namn (en)", value -> filter.enName = value));
        filterRow.getCell(abbrevCol).setComponent(
                textFilter("Förkortning", value -> filter.abbreviation = value));
        filterRow.getCell(kindCol).setComponent(
                textFilter("Typ", value -> filter.unitKind = value));
        filterRow.getCell(inSystemCol).setComponent(
                booleanFilter(value -> filter.inSystem = value));
        filterRow.getCell(legacyCol).setComponent(
                booleanFilter(value -> filter.legacyUnit = value));
        filterRow.getCell(ecoCol).setComponent(
                booleanFilter(value -> filter.courseEconomyHolder = value));
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

    /** A three-state Select (Alla / Ja / Nej) mapped to a nullable Boolean. */
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

    private void applyFilter() {
        if (dataView != null) {
            dataView.setFilter(filter::test);
        }
    }

    private void buildEditorComponents(Grid.Column<OrganisationUnit> svNameCol,
                                       Grid.Column<OrganisationUnit> enNameCol,
                                       Grid.Column<OrganisationUnit> abbrevCol,
                                       Grid.Column<OrganisationUnit> kindCol,
                                       Grid.Column<OrganisationUnit> inSystemCol,
                                       Grid.Column<OrganisationUnit> legacyCol,
                                       Grid.Column<OrganisationUnit> ecoCol,
                                       Grid.Column<OrganisationUnit> actionsCol) {
        TextField svNameField = new TextField();
        svNameField.setWidthFull();
        binder.forField(svNameField)
                .asRequired("Swedish name is required")
                .bind(OrganisationUnit::getSvName, OrganisationUnit::setSvName);
        svNameCol.setEditorComponent(svNameField);

        TextField enNameField = new TextField();
        enNameField.setWidthFull();
        binder.forField(enNameField)
                .asRequired("English name is required")
                .bind(OrganisationUnit::getEnName, OrganisationUnit::setEnName);
        enNameCol.setEditorComponent(enNameField);

        TextField abbrevField = new TextField();
        abbrevField.setWidthFull();
        binder.forField(abbrevField)
                .bind(OrganisationUnit::getAbbreviation, OrganisationUnit::setAbbreviation);
        abbrevCol.setEditorComponent(abbrevField);

        TextField kindField = new TextField();
        kindField.setWidthFull();
        binder.forField(kindField)
                .asRequired("Unit kind is required")
                .bind(OrganisationUnit::getUnitKind, OrganisationUnit::setUnitKind);
        kindCol.setEditorComponent(kindField);

        Checkbox inSystemField = new Checkbox();
        binder.forField(inSystemField)
                .bind(ou -> Boolean.TRUE.equals(ou.getInSystem()), OrganisationUnit::setInSystem);
        inSystemCol.setEditorComponent(inSystemField);

        Checkbox legacyField = new Checkbox();
        binder.forField(legacyField)
                .bind(ou -> Boolean.TRUE.equals(ou.getLegacyUnit()), OrganisationUnit::setLegacyUnit);
        legacyCol.setEditorComponent(legacyField);

        Checkbox ecoField = new Checkbox();
        binder.forField(ecoField)
                .bind(ou -> Boolean.TRUE.equals(ou.getCourseEconomyHolder()),
                        OrganisationUnit::setCourseEconomyHolder);
        ecoCol.setEditorComponent(ecoField);

        actionsCol.setEditorComponent(buildEditorActions(this::save));
    }

    private void configureEditor() {
        editor.setBinder(binder);
        editor.setBuffered(true);
        editor.addCancelListener(e -> discardPendingNew());
    }

    private HorizontalLayout buildRowActions(OrganisationUnit unit) {
        Button edit = new Button("Edit", e -> editItem(unit));

        Button delete = new Button("Delete", e -> delete(unit));
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        delete.setEnabled(unit.getId() != null);

        // Toggle the nested hierarchy grid for this unit.
        Button toggle = new Button("Hierarki", e -> grid.setDetailsVisible(unit,
                !grid.isDetailsVisible(unit)));
        toggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        // The nested grid lists sub-units, which only exist for a persisted unit.
        toggle.setEnabled(unit.getId() != null);

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

    /* ---- Nested grid (YearsOfHierarchy) ---- */

    /**
     * Builds the details panel shown when a unit row is expanded: a heading, a
     * toolbar to add a hierarchy row, and an inline-editable grid of the unit's
     * {@link YearsOfHierarchy} super-unit relationships (rows where this unit is
     * the sub-unit, i.e. which parent unit it reports to each year).
     */
    private Component buildHierarchyPanel(OrganisationUnit unit) {
        Grid<YearsOfHierarchy> yohGrid = new Grid<>(YearsOfHierarchy.class, false);
        Binder<YearsOfHierarchy> yohBinder = new Binder<>(YearsOfHierarchy.class);
        Editor<YearsOfHierarchy> yohEditor = yohGrid.getEditor();
        yohEditor.setBinder(yohBinder);
        yohEditor.setBuffered(true);

        yohGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        yohGrid.setAllRowsVisible(true);

        Grid.Column<YearsOfHierarchy> superUnitCol = yohGrid.addColumn(this::superUnitLabel)
                .setHeader("Överenhet").setAutoWidth(true);
        Grid.Column<YearsOfHierarchy> firstYearCol = yohGrid.addColumn(YearsOfHierarchy::getFirstYear)
                .setHeader("Första år").setAutoWidth(true);
        Grid.Column<YearsOfHierarchy> lastYearCol = yohGrid.addColumn(YearsOfHierarchy::getLastYear)
                .setHeader("Sista år").setAutoWidth(true);
        Grid.Column<YearsOfHierarchy> noteCol = yohGrid.addColumn(YearsOfHierarchy::getNote)
                .setHeader("Anteckningar").setAutoWidth(true);
        Grid.Column<YearsOfHierarchy> actionsCol = yohGrid.addComponentColumn(
                yoh -> buildHierarchyRowActions(unit, yohGrid, yohEditor, yoh))
                .setHeader("Handling").setAutoWidth(true).setFlexGrow(0);

        // Editors for the hierarchy columns.
        ComboBox<OrganisationUnit> superUnitField = new ComboBox<>();
        superUnitField.setItems(safeList(organisationUnitService::getAllOrganisationUnits));
        superUnitField.setItemLabelGenerator(this::unitLabel);
        superUnitField.setWidthFull();
        yohBinder.forField(superUnitField)
                .asRequired("Super-unit is required")
                .bind(YearsOfHierarchy::getSuperUnit, YearsOfHierarchy::setSuperUnit);
        superUnitCol.setEditorComponent(superUnitField);

        IntegerField firstYearField = new IntegerField();
        firstYearField.setWidthFull();
        yohBinder.forField(firstYearField)
                .asRequired("First year is required")
                .bind(YearsOfHierarchy::getFirstYear, YearsOfHierarchy::setFirstYear);
        firstYearCol.setEditorComponent(firstYearField);

        IntegerField lastYearField = new IntegerField();
        lastYearField.setWidthFull();
        // lastYear is nullable (open-ended); no asRequired.
        yohBinder.forField(lastYearField)
                .bind(YearsOfHierarchy::getLastYear, YearsOfHierarchy::setLastYear);
        lastYearCol.setEditorComponent(lastYearField);

        TextField yohNoteField = new TextField();
        yohNoteField.setWidthFull();
        yohBinder.forField(yohNoteField)
                .bind(YearsOfHierarchy::getNote, YearsOfHierarchy::setNote);
        noteCol.setEditorComponent(yohNoteField);

        actionsCol.setEditorComponent(buildHierarchyEditorActions(unit, yohGrid, yohEditor));

        yohGrid.setItems(superUnitRows(unit));

        Button addYoh = new Button("Ny relation", e -> {
            if (yohEditor.isOpen()) {
                yohEditor.cancel();
            }
            YearsOfHierarchy fresh = new YearsOfHierarchy();
            // This unit is always the sub-unit of its own super-unit rows.
            fresh.setSubUnit(unit);
            List<YearsOfHierarchy> rows = superUnitRows(unit);
            rows.add(0, fresh);
            yohGrid.setItems(rows);
            yohEditor.editItem(fresh);
        });
        addYoh.addThemeVariants(ButtonVariant.LUMO_SMALL);

        VerticalLayout panel = new VerticalLayout(
                new H4("Inkluderande enheter över åren"),
                new HorizontalLayout(addYoh),
                yohGrid);
        panel.setPadding(true);
        panel.setSpacing(true);
        return panel;
    }

    private HorizontalLayout buildHierarchyRowActions(OrganisationUnit unit,
                                                      Grid<YearsOfHierarchy> yohGrid,
                                                      Editor<YearsOfHierarchy> yohEditor,
                                                      YearsOfHierarchy yoh) {
        Button edit = new Button("Edit", e -> {
            if (!yohEditor.isOpen()) {
                yohEditor.editItem(yoh);
            }
        });
        Button delete = new Button("Delete",
                e -> deleteHierarchy(unit, yohGrid, yohEditor, yoh));
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout actions = new HorizontalLayout(edit, delete);
        actions.setPadding(false);
        return actions;
    }

    private HorizontalLayout buildHierarchyEditorActions(OrganisationUnit unit,
                                                         Grid<YearsOfHierarchy> yohGrid,
                                                         Editor<YearsOfHierarchy> yohEditor) {
        Button save = new Button("Save", e -> saveHierarchy(unit, yohGrid, yohEditor));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        save.addClickShortcut(Key.ENTER);

        Button cancel = new Button("Cancel", e -> yohEditor.cancel());
        cancel.addThemeVariants(ButtonVariant.LUMO_SMALL);

        HorizontalLayout actions = new HorizontalLayout(save, cancel);
        actions.setPadding(false);
        return actions;
    }

    /* ---- Master data / actions ---- */

    private void editItem(OrganisationUnit unit) {
        if (editor.isOpen()) {
            editor.cancel();
        }
        editor.editItem(unit);
    }

    private void addUnit() {
        if (editor.isOpen()) {
            editor.cancel();
        }
        OrganisationUnit fresh = new OrganisationUnit();
        units.add(0, fresh);
        grid.getListDataView().refreshAll();
        pendingNew = fresh;
        editor.editItem(fresh);
    }

    private void loadUnits() {
        try {
            units.clear();
            units.addAll(organisationUnitService.getAllOrganisationUnits());
            dataView = grid.setItems(units);
            applyFilter();
        } catch (Exception ex) {
            log.error("Failed to load organisation units", ex);
            notifyError("Could not load organisation units: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    private void save() {
        OrganisationUnit edited = editor.getItem();
        if (edited == null) {
            return;
        }
        if (!editor.save()) {
            notifyError("Please fix the highlighted fields.");
            return;
        }
        try {
            organisationUnitService.saveOrganisationUnit(edited);
            pendingNew = null;
            notifySuccess("Organisation unit saved: " + edited.getSvName());
            loadUnits();
        } catch (Exception ex) {
            log.error("Failed to save organisation unit", ex);
            notifyError("Could not save organisation unit: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    private void delete(OrganisationUnit unit) {
        if (unit == null) {
            return;
        }
        try {
            organisationUnitService.deleteOrganisationUnit(unit);
            notifySuccess("Organisation unit deleted.");
            loadUnits();
        } catch (Exception ex) {
            log.error("Failed to delete organisation unit", ex);
            notifyError("Could not delete organisation unit: " + ex.getMessage());
        }
    }

    private void discardPendingNew() {
        if (pendingNew != null) {
            units.remove(pendingNew);
            pendingNew = null;
            grid.getListDataView().refreshAll();
        }
    }

    /* ---- Hierarchy data / actions ---- */

    /**
     * Commits the hierarchy editor and persists the {@link YearsOfHierarchy}
     * entity. The sub-unit back-reference is pinned to the owning unit first
     * (it is {@code @NotNull} and the FK owner on this side).
     */
    @PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    private void saveHierarchy(OrganisationUnit unit, Grid<YearsOfHierarchy> yohGrid,
                               Editor<YearsOfHierarchy> yohEditor) {
        YearsOfHierarchy edited = yohEditor.getItem();
        if (edited == null) {
            return;
        }
        if (!yohEditor.save()) {
            notifyError("Please fix the highlighted fields.");
            return;
        }
        edited.setSubUnit(unit);
        try {
            organisationUnitService.saveYearsOfHierarchy(edited);
            notifySuccess("Hierarchy saved.");
            reloadHierarchy(unit, yohGrid);
        } catch (Exception ex) {
            log.error("Failed to save hierarchy", ex);
            notifyError("Could not save hierarchy: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    private void deleteHierarchy(OrganisationUnit unit, Grid<YearsOfHierarchy> yohGrid,
                                 Editor<YearsOfHierarchy> yohEditor, YearsOfHierarchy yoh) {
        if (yoh == null) {
            return;
        }
        if (yohEditor.isOpen()) {
            yohEditor.cancel();
        }
        try {
            if (yoh.getId() != null) {
                organisationUnitService.deleteYearsOfHierarchy(yoh.getId());
            }
            // A never-persisted new row simply drops out on reload below.
            notifySuccess("Hierarchy deleted.");
            reloadHierarchy(unit, yohGrid);
        } catch (Exception ex) {
            log.error("Failed to delete hierarchy", ex);
            notifyError("Could not delete hierarchy: " + ex.getMessage());
        }
    }

    /** Refreshes the nested grid from the database for the given unit. */
    private void reloadHierarchy(OrganisationUnit unit, Grid<YearsOfHierarchy> yohGrid) {
        if (unit == null || yohGrid == null) {
            return;
        }
        try {
            // Re-read the unit from the fresh unit list so the sub-unit set is
            // current (the service has no single-unit getter).
            OrganisationUnit fresh = organisationUnitService.getAllOrganisationUnits().stream()
                    .filter(u -> u.getId() != null && u.getId().equals(unit.getId()))
                    .findFirst()
                    .orElse(unit);
            yohGrid.setItems(superUnitRows(fresh));
        } catch (Exception ex) {
            yohGrid.setItems(superUnitRows(unit));
        }
    }

    /* ---- Helpers ---- */

    /** The super-unit (parent) relationships this unit owns as sub-unit. */
    private List<YearsOfHierarchy> superUnitRows(OrganisationUnit unit) {
        if (unit == null || unit.getSuperUnits() == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(unit.getSuperUnits());
    }

    private String superUnitLabel(YearsOfHierarchy yoh) {
        return yoh == null ? "" : unitLabel(yoh.getSuperUnit());
    }

    private String unitLabel(OrganisationUnit unit) {
        if (unit == null) {
            return "";
        }
        if (unit.getAbbreviation() != null && !unit.getAbbreviation().isBlank()) {
            return unit.getAbbreviation() + " - " + unit.getSvName();
        }
        return unit.getSvName() == null ? "" : unit.getSvName();
    }

    private String boolLabel(Boolean value) {
        return Boolean.TRUE.equals(value) ? "Ja" : "Nej";
    }

    /** Runs a throwing supplier, returning an empty list on failure (for selectors). */
    private <T> List<T> safeList(Callable<List<T>> supplier) {
        try {
            return supplier.call();
        } catch (Exception ex) {
            log.error("Failed to load selector items", ex);
            return new ArrayList<>();
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
    private static class OuFilter {
        private String svName;
        private String enName;
        private String abbreviation;
        private String unitKind;
        private Boolean inSystem;           // null = any, true = Ja, false = Nej
        private Boolean legacyUnit;         // null = any, true = Ja, false = Nej
        private Boolean courseEconomyHolder; // null = any, true = Ja, false = Nej

        boolean test(OrganisationUnit ou) {
            return matchesText(svName, ou.getSvName())
                    && matchesText(enName, ou.getEnName())
                    && matchesText(abbreviation, ou.getAbbreviation())
                    && matchesText(unitKind, ou.getUnitKind())
                    && matchesBoolean(inSystem, ou.getInSystem())
                    && matchesBoolean(legacyUnit, ou.getLegacyUnit())
                    && matchesBoolean(courseEconomyHolder, ou.getCourseEconomyHolder());
        }

        private boolean matchesText(String needle, String value) {
            if (needle == null || needle.isBlank()) {
                return true;
            }
            return value != null
                    && value.toLowerCase().contains(needle.toLowerCase().trim());
        }

        /** A null flag on the entity is treated as false ("Nej"), matching the grid label. */
        private boolean matchesBoolean(Boolean wanted, Boolean value) {
            return wanted == null || wanted == Boolean.TRUE.equals(value);
        }
    }
}
