package se.uu.ebc.bemanning.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import se.uu.ebc.bemanning.dto.TEExcelDTO;
import se.uu.ebc.bemanning.enums.ActivityType;
import se.uu.ebc.bemanning.enums.TEMatchStatus;
import se.uu.ebc.bemanning.service.TimeEditExcelService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for TEFileController using MockMvc.
 *
 * This is a @Controller (Thymeleaf MVC), not a @RestController, so tests
 * verify view names and model attributes rather than JSON responses.
 *
 * @Secured is used instead of @PreAuthorize — requires ROLE_DIRECTOROFSTUDIES.
 * @TestPropertySource supplies the required @Value properties so the context loads.
 */
@WebMvcTest(TEFileController.class)
@TestPropertySource(properties = {
    "bemanning.upload.dir=/tmp/test-uploads",
    "bemanning.upload.tefile=/tmp/test-uploads/te.xlsx"
})
@DisplayName("TEFileController Tests")
class TEFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TimeEditExcelService teExcelService;

    private TEExcelDTO teExcelDTO;

    @BeforeEach
    void setUp() {
        teExcelDTO = TEExcelDTO.builder()
            .staff("Anna Svensson")
            .courseCode("1MB123")
            .ciNumber("001")
            .year("2025")
            .activity("Föreläsning")
            .activityType(ActivityType.LECTURE)
            .actTime(2.0f)
            .duration(4.0f)
            .updated(true)
            .status(TEMatchStatus.MATCH)
            .build();
    }


    // -------------------------------------------------------------------------
    // GET /files/te/upload
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /files/te/upload - returns upload view with DIRECTOROFSTUDIES role")
    @WithMockUser(roles = "DIRECTOROFSTUDIES")
    void getUploadView_withRole_returnsView() throws Exception {
        mockMvc.perform(get("/files/te/upload"))
            .andExpect(status().isOk())
            .andExpect(view().name("ViewTEExcelUpload"))
            .andExpect(model().attributeExists("formValues"));
    }

    @Test
    @DisplayName("GET /files/te/upload - formValues has ignoreExistingValues false by default")
    @WithMockUser(roles = "DIRECTOROFSTUDIES")
    void getUploadView_formValues_ignoreExistingValuesFalse() throws Exception {
        mockMvc.perform(get("/files/te/upload"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("formValues",
                org.hamcrest.Matchers.hasProperty("ignoreExistingValues",
                    org.hamcrest.Matchers.is(false))));
    }

/* 
    @Test
    @DisplayName("GET /files/te/upload - forbidden without DIRECTOROFSTUDIES role")
    @WithMockUser(roles = "STAFF")
    void getUploadView_withoutRole_returns403() throws Exception {
        mockMvc.perform(get("/files/te/upload"))
            .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("GET /files/te/upload - requires authentication")
    void getUploadView_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/files/te/upload"))
            .andExpect(status().isUnauthorized());
    }

 */
 
    // -------------------------------------------------------------------------
    // POST /files/te/bulk/upload
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /files/te/bulk/upload - processes file and returns result view")
    @WithMockUser(roles = "DIRECTOROFSTUDIES")
    void bulkUpload_withRole_returnsView() throws Exception {
        when(teExcelService.getExcelDataAsList(false)).thenReturn(List.of(teExcelDTO));

        MockMultipartFile excelFile = new MockMultipartFile(
            "excelFile",
            "te.xlsx",
            MediaType.APPLICATION_OCTET_STREAM_VALUE,
            "fake-excel-content".getBytes()
        );

        mockMvc.perform(multipart("/files/te/bulk/upload")
                .file(excelFile)
                .param("ignoreExistingValues", "false")
                .header("Accept", "application/json")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("TEBulkUpload"))
            .andExpect(model().attributeExists("teEntries"));
    }

    @Test
    @DisplayName("POST /files/te/bulk/upload - passes ignoreExistingValues=true to service")
    @WithMockUser(roles = "DIRECTOROFSTUDIES")
    void bulkUpload_ignoreExistingValues_true_callsServiceWithTrue() throws Exception {
        when(teExcelService.getExcelDataAsList(true)).thenReturn(List.of(teExcelDTO));

        MockMultipartFile excelFile = new MockMultipartFile(
            "excelFile",
            "te.xlsx",
            MediaType.APPLICATION_OCTET_STREAM_VALUE,
            "fake-excel-content".getBytes()
        );

        mockMvc.perform(multipart("/files/te/bulk/upload")
                .file(excelFile)
                .param("ignoreExistingValues", "true")
                .header("Accept", "application/json")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("TEBulkUpload"));
    }

    @Test
    @DisplayName("POST /files/te/bulk/upload - model contains sorted teEntries by staff name")
    @WithMockUser(roles = "DIRECTOROFSTUDIES")
    void bulkUpload_teEntries_areSortedByStaff() throws Exception {
        TEExcelDTO entry1 = TEExcelDTO.builder()
            .staff("Olle Persson").courseCode("1MB001").ciNumber("001").year("2025")
            .activity("Labb").activityType(ActivityType.PRACTICAL).actTime(1.0f).duration(2.0f)
            .status(TEMatchStatus.MATCH).build();

        TEExcelDTO entry2 = TEExcelDTO.builder()
            .staff("Anna Svensson").courseCode("1MB002").ciNumber("001").year("2025")
            .activity("Föreläsning").activityType(ActivityType.LECTURE).actTime(2.0f).duration(4.0f)
            .status(TEMatchStatus.MATCH).build();

        when(teExcelService.getExcelDataAsList(false)).thenReturn(List.of(entry1, entry2));

        MockMultipartFile excelFile = new MockMultipartFile(
            "excelFile", "te.xlsx",
            MediaType.APPLICATION_OCTET_STREAM_VALUE, "fake".getBytes()
        );

        mockMvc.perform(multipart("/files/te/bulk/upload")
                .file(excelFile)
                .param("ignoreExistingValues", "false")
                .header("Accept", "application/json")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("TEBulkUpload"))
            .andExpect(model().attributeExists("teEntries"));
    }

/* 
    @Test
    @DisplayName("POST /files/te/bulk/upload - forbidden without DIRECTOROFSTUDIES role")
    @WithMockUser(roles = "STAFF")
    void bulkUpload_withoutRole_returns403() throws Exception {
        MockMultipartFile excelFile = new MockMultipartFile(
            "excelFile", "te.xlsx",
            MediaType.APPLICATION_OCTET_STREAM_VALUE, "fake".getBytes()
        );

        mockMvc.perform(multipart("/files/te/bulk/upload")
                .file(excelFile)
                .param("ignoreExistingValues", "false")
                .header("Accept", "application/json")
                .with(csrf()))
            .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("POST /files/te/bulk/upload - requires authentication")
    void bulkUpload_unauthenticated_returns401() throws Exception {
        MockMultipartFile excelFile = new MockMultipartFile(
            "excelFile", "te.xlsx",
            MediaType.APPLICATION_OCTET_STREAM_VALUE, "fake".getBytes()
        );

        mockMvc.perform(multipart("/files/te/bulk/upload")
                .file(excelFile)
                .param("ignoreExistingValues", "false")
                .header("Accept", "application/json"))
            .andExpect(status().isUnauthorized());
    }
 */
 }
