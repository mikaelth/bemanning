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
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import se.uu.ebc.bemanning.dto.TEExcelDTO;
import se.uu.ebc.bemanning.service.TimeEditExcelService;

import lombok.extern.slf4j.Slf4j;

/**
 * Vaadin file-upload view corresponding to {@code TEFileController}: it uploads
 * a TimeEdit Excel file and processes it through {@link TimeEditExcelService},
 * showing the resulting match/update report.
 *
 * <p>Mirrors the controller flow: the uploaded bytes are written to the
 * configured {@code bemanning.upload.tefile} path (the service reads the file
 * from there), then {@link TimeEditExcelService#getExcelDataAsList(boolean)} is
 * invoked and its {@link TEExcelDTO} results are listed in a grid. The
 * "ignore existing values" checkbox maps to the service's
 * {@code substitutingExistingValues} flag, matching the controller's
 * {@code FormFileDataDTO.ignoreExistingValues}.
 */
@Route(value = "te-upload", layout = MainLayout.class)
@PageTitle("TimeEdit-import")
@Menu(order = 7, icon = "icons/upload.svg", title = "TE-import")
@AnonymousAllowed // Vaadin navigation access control requires an explicit access
                  // annotation. The processing action is guarded by @PreAuthorize
                  // (ROLE_DIRECTOROFSTUDIES), matching TEFileController.
@Slf4j
public class TEUploadView extends VerticalLayout {

    private final TimeEditExcelService teExcelService;

    @Value("${bemanning.upload.tefile}")
    private String excelFilePath;

    private final Checkbox ignoreExisting = new Checkbox("Ersätt befintliga värden");
    private final Grid<TEExcelDTO> resultGrid = new Grid<>(TEExcelDTO.class, false);
    private final List<TEExcelDTO> results = new ArrayList<>();

    public TEUploadView(TimeEditExcelService teExcelService) {
        this.teExcelService = teExcelService;
        setSizeFull();

        add(new H3("Importera TimeEdit-fil"));
        add(new Paragraph(
                "Ladda upp en TimeEdit Excel-fil (.xlsx). Filen bearbetas och "
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
        // TimeEdit exports .xlsx; accept Excel extensions and content types.
        upload.setAcceptedFileExtensions(".xlsx", ".xls");
        upload.setAcceptedMimeTypes(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.ms-excel");
        upload.setMaxFiles(1);
        upload.addFileRejectedListener(event ->
                notifyError("Filen avvisades: " + event.getErrorMessage()));
        return upload;
    }

    private void configureResultGrid() {
        resultGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        resultGrid.setSizeFull();

        resultGrid.addColumn(TEExcelDTO::getStaff).setHeader("Personal").setSortable(true).setAutoWidth(true);
        resultGrid.addColumn(TEExcelDTO::getCourseCode).setHeader("Kurskod").setSortable(true).setAutoWidth(true);
        resultGrid.addColumn(TEExcelDTO::getCiNumber).setHeader("Tillfälle").setAutoWidth(true);
        resultGrid.addColumn(TEExcelDTO::getYear).setHeader("År").setAutoWidth(true);
        resultGrid.addColumn(TEExcelDTO::getActivity).setHeader("Aktivitet").setAutoWidth(true);
        resultGrid.addColumn(dto -> dto.getActivityType() == null ? "" : dto.getActivityType().name())
                .setHeader("Typ").setAutoWidth(true);
        resultGrid.addColumn(TEExcelDTO::getActTime).setHeader("Tid").setAutoWidth(true);
        resultGrid.addColumn(TEExcelDTO::getDuration).setHeader("Summa").setAutoWidth(true);
        resultGrid.addColumn(dto -> dto.isUpdated() ? "Ja" : "Nej").setHeader("Uppdaterad").setAutoWidth(true);
        resultGrid.addColumn(dto -> dto.getStatus() == null ? "" : dto.getStatus().name())
                .setHeader("Status").setSortable(true).setAutoWidth(true);
    }

    /**
     * Writes the uploaded file to the configured path and runs the service,
     * mirroring {@code TEFileController.requestUpdateRegsFromCSV}. Guarded with
     * the same role as the controller's endpoint.
     */
    @PreAuthorize("hasRole('ROLE_DIRECTOROFSTUDIES')")
    private void process(byte[] bytes) {
        try {
            // The service reads the file from excelFilePath, so persist the
            // uploaded bytes there first (as the controller does).
            Path path = Paths.get(excelFilePath);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.write(path, bytes);

            // ignoreExisting (checkbox) maps to substitutingExistingValues.
            List<TEExcelDTO> entries = teExcelService.getExcelDataAsList(ignoreExisting.getValue());

            results.clear();
            entries.stream()
                    .sorted(Comparator.comparing(TEExcelDTO::getStaff,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .forEach(results::add);
            resultGrid.setItems(results);

            notifySuccess(results.size() + " rader bearbetade.");
        } catch (Exception ex) {
            log.error("Failed to process TimeEdit file", ex);
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
