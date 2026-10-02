package se.uu.ebc.bemanning.ui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.server.streams.UploadHandler;

import se.uu.ebc.bemanning.dto.PrimulaEntriesExcel;
import se.uu.ebc.bemanning.service.PrimulaExcelService;

import lombok.extern.slf4j.Slf4j;

/**
 * Vaadin file-upload view corresponding to {@code PrimulaFileController}: it
 * uploads a Primula Excel file and processes it through
 * {@link PrimulaExcelService}, showing the resulting entries.
 *
 * <p>Mirrors the controller flow: the uploaded bytes are written to the
 * configured {@code bemanning.upload.primulafile} path (the service reads the
 * file from there), then {@link PrimulaExcelService#getExcelDataAsList(boolean)}
 * is invoked and its {@link PrimulaEntriesExcel} results are listed in a grid.
 * The "ersätt befintliga värden" checkbox maps to the service's
 * {@code overWrite} flag, matching the controller's
 * {@code FormFileDataDTO.ignoreExistingValues}.
 */
// Embedded as a component inside UploadView (one of its tabs); not a route of
// its own. Declared as a (prototype) Spring bean so @Value and @PreAuthorize
// still apply; UploadView receives it via constructor injection. The processing
// action remains guarded by @PreAuthorize (ROLE_PRIMULAADMIN).
@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@Slf4j
public class PrimulaUploadView extends VerticalLayout {

    private final PrimulaExcelService primulaExcelService;

    @Value("${bemanning.upload.primulafile}")
    private String excelFilePath;

    private final Checkbox ignoreExisting = new Checkbox("Ersätt befintliga värden");
    private final Grid<PrimulaEntriesExcel> resultGrid = new Grid<>(PrimulaEntriesExcel.class, false);
    private final List<PrimulaEntriesExcel> results = new ArrayList<>();

    public PrimulaUploadView(PrimulaExcelService primulaExcelService) {
        this.primulaExcelService = primulaExcelService;
        setSizeFull();

        add(new H3("Importera Primula-fil"));
        add(new Paragraph(
                "Ladda upp en Primula Excel-fil (.xls). Filen bearbetas och "
                        + "resultatet visas nedan."));

        add(ignoreExisting);
        add(buildUpload());
        configureResultGrid();
        add(resultGrid);
    }

    private Upload buildUpload() {
        // Modern (Vaadin 25) in-memory upload handler: the callback receives the
        // uploaded bytes server-side when the transfer completes. It runs off the
        // UI thread, so UI updates are wrapped in ui.access(...).
        UploadHandler handler = UploadHandler.inMemory((metadata, bytes) ->
                getUI().ifPresent(ui -> ui.access(() -> process(bytes))));
        Upload upload = new Upload(handler);
        // Primula exports .xls; accept Excel extensions and content types.
        upload.setAcceptedFileExtensions(".xls", ".xlsx");
        upload.setAcceptedMimeTypes(
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        upload.setMaxFiles(1);
        upload.addFileRejectedListener(event ->
                notifyError("Filen avvisades: " + event.getErrorMessage()));
        return upload;
    }

    private void configureResultGrid() {
        resultGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        resultGrid.setSizeFull();

        resultGrid.addColumn(PrimulaEntriesExcel::getRowIndex).setHeader("Rad").setAutoWidth(true);
        resultGrid.addColumn(PrimulaEntriesExcel::getPNIN)
                .setHeader("Personnr").setSortable(true).setAutoWidth(true);
        resultGrid.addColumn(PrimulaEntriesExcel::getOmf).setHeader("Omf").setAutoWidth(true);
        resultGrid.addColumn(PrimulaEntriesExcel::getKOmf).setHeader("Komf").setAutoWidth(true);
        resultGrid.addColumn(PrimulaEntriesExcel::getCost).setHeader("Lön+LKB").setAutoWidth(true);
//        resultGrid.addColumn(this::hourlyCostLabel).setHeader("Timkostnad").setSortable(true).setAutoWidth(true);
        resultGrid.addColumn(PrimulaEntriesExcel::hourlyCost).setHeader("Timkostnad").setSortable(true).setAutoWidth(true);
        resultGrid.addColumn(dto -> dto.isUpdated() ? "Ja" : "Nej")
                .setHeader("Uppdaterad").setSortable(true).setAutoWidth(true);
    }

    /** Guards hourlyCost(), which divides by omf/komf and can throw/NPE. */
    private String hourlyCostLabel(PrimulaEntriesExcel dto) {
        try {
            return String.valueOf(dto.hourlyCost());
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * Writes the uploaded file to the configured path and runs the service,
     * mirroring {@code PrimulaFileController.requestUpdateRegsFromCSV}. Guarded
     * with the same role as the controller's endpoint.
     */
    @PreAuthorize("hasRole('ROLE_PRIMULAADMIN')")
    private void process(byte[] bytes) {
        try {
            // The service reads the file from excelFilePath, so persist the
            // uploaded bytes there first (as the controller does).
            Path path = Paths.get(excelFilePath);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.write(path, bytes);

            // ignoreExisting (checkbox) maps to the service's overWrite flag.
            List<PrimulaEntriesExcel> entries =
                    primulaExcelService.getExcelDataAsList(ignoreExisting.getValue());

            results.clear();
            entries.stream()
                    .sorted(Comparator.comparing(PrimulaEntriesExcel::getPNIN,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .forEach(results::add);
            resultGrid.setItems(results);

            notifySuccess(results.size() + " rader bearbetade.");
        } catch (Exception ex) {
            log.error("Failed to process Primula file", ex);
            notifyError("Kunde inte bearbeta filen: " + ex.getMessage());
        }
    }

    private void notifySuccess(String message) {
        Notification n = Notification.show(message, 3000, Notification.Position.BOTTOM_START);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void notifyError(String message) {
        Notification n = Notification.show(message, 6000, Notification.Position.BOTTOM_START);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
