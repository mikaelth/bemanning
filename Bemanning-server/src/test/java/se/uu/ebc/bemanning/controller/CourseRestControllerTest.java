package se.uu.ebc.bemanning.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import se.uu.ebc.bemanning.dto.CourseDTO;
import se.uu.ebc.bemanning.service.CourseService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for CourseRestController using MockMvc.
 *
 * @WebMvcTest loads only the web layer (controller + security), mocking all services.
 * @MockitoBean is the Spring Boot 4.x replacement for @MockBean.
 * @WithMockUser simulates an authenticated user without a real security context.
 */
@WebMvcTest(CourseRestController.class)
@DisplayName("CourseRestController Tests")
class CourseRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ObjectMapper is needed for JSON serialization in REST API tests
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CourseService courseService;

    private CourseDTO courseDTO;

    @BeforeEach
    void setUp() {
        courseDTO = new CourseDTO();
        courseDTO.setId(1L);
        courseDTO.setCode("1MB123");
        courseDTO.setSeName("Molekylärbiologi");
        courseDTO.setEnName("Molecular Biology");
        courseDTO.setCourseGroup("Biology");
        courseDTO.setCredits(7.5f);
        courseDTO.setNote("Introductory course");
    }


    // -------------------------------------------------------------------------
    // GET /rest/courses
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /rest/courses - returns list of courses")
    @WithMockUser
    void getAllCourses_returnsOk() throws Exception {
        when(courseService.getAllCourses()).thenReturn(List.of(courseDTO));

        mockMvc.perform(get("/rest/courses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.courses[0].id").value(1L))
            .andExpect(jsonPath("$.courses[0].code").value("1MB123"))
            .andExpect(jsonPath("$.courses[0].seName").value("Molekylärbiologi"))
            .andExpect(jsonPath("$.courses[0].enName").value("Molecular Biology"))
            .andExpect(jsonPath("$.courses[0].credits").value(7.5));
    }

    @Test
    @DisplayName("GET /rest/courses - returns empty list when no courses")
    @WithMockUser
    void getAllCourses_returnsEmptyList() throws Exception {
        when(courseService.getAllCourses()).thenReturn(List.of());

        mockMvc.perform(get("/rest/courses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.courses").isEmpty());
    }

/* 
    @Test
    @DisplayName("GET /rest/courses - requires authentication")
    void getAllCourses_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/rest/courses"))
            .andExpect(status().isUnauthorized());
    }
 */


    // -------------------------------------------------------------------------
    // GET /rest/courses/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /rest/courses/{id} - returns course by id")
    @WithMockUser
    void getCourseById_returnsOk() throws Exception {
        when(courseService.getById(1L)).thenReturn(courseDTO);

        mockMvc.perform(get("/rest/courses/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.code").value("1MB123"))
            .andExpect(jsonPath("$.seName").value("Molekylärbiologi"));
    }


    // -------------------------------------------------------------------------
    // POST /rest/courses
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /rest/courses - creates course with COREDATAADMIN role")
    @WithMockUser(roles = "COREDATAADMIN")
    void createCourse_withAdminRole_returnsOk() throws Exception {
        CourseDTO newCourse = new CourseDTO();
        newCourse.setCode("2MB456");
        newCourse.setSeName("Cellbiologi");
        newCourse.setEnName("Cell Biology");
        newCourse.setCredits(5.0f);

        CourseDTO savedCourse = new CourseDTO();
        savedCourse.setId(2L);
        savedCourse.setCode("2MB456");
        savedCourse.setSeName("Cellbiologi");
        savedCourse.setEnName("Cell Biology");
        savedCourse.setCredits(5.0f);

        when(courseService.saveCourse(any(CourseDTO.class))).thenReturn(savedCourse);

        mockMvc.perform(post("/rest/courses")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCourse)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sucess").value(true))
            .andExpect(jsonPath("$.courses.id").value(2L))
            .andExpect(jsonPath("$.courses.code").value("2MB456"))
            .andExpect(jsonPath("$.courses.seName").value("Cellbiologi"));
    }

/* 
    @Test
    @DisplayName("POST /rest/courses - forbidden without COREDATAADMIN role")
    @WithMockUser(roles = "STAFF")
    void createCourse_withoutAdminRole_returns403() throws Exception {
        mockMvc.perform(post("/rest/courses")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDTO)))
            .andExpect(status().isForbidden());
    }
 */


    // -------------------------------------------------------------------------
    // PUT /rest/courses/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("PUT /rest/courses/{id} - updates course with COREDATAADMIN role")
    @WithMockUser(roles = "COREDATAADMIN")
    void updateCourse_withAdminRole_returnsOk() throws Exception {
        when(courseService.saveCourse(any(CourseDTO.class))).thenReturn(courseDTO);

        mockMvc.perform(put("/rest/courses/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sucess").value(true))
            .andExpect(jsonPath("$.courses.id").value(1L));
    }

    @Test
    @DisplayName("PUT /rest/courses/{id} - returns 400 when path id and body id mismatch")
    @WithMockUser(roles = "COREDATAADMIN")
    void updateCourse_idMismatch_returns400() throws Exception {
        mockMvc.perform(put("/rest/courses/99")  // path id = 99, body id = 1
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDTO)))
            .andExpect(status().isBadRequest());
    }

/* 
    @Test
    @DisplayName("PUT /rest/courses/{id} - forbidden without COREDATAADMIN role")
    @WithMockUser(roles = "STAFF")
    void updateCourse_withoutAdminRole_returns403() throws Exception {
        mockMvc.perform(put("/rest/courses/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDTO)))
            .andExpect(status().isForbidden());
    }

 */

    // -------------------------------------------------------------------------
    // DELETE /rest/courses/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("DELETE /rest/courses/{id} - deletes course with COREDATAADMIN role")
    @WithMockUser(roles = "COREDATAADMIN")
    void deleteCourse_withAdminRole_returnsOk() throws Exception {
        doNothing().when(courseService).deleteCourse(1L);

        mockMvc.perform(delete("/rest/courses/1").with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sucess").value(true))
            .andExpect(jsonPath("$.id").value(1L));

        verify(courseService).deleteCourse(1L);
    }

/* 
    @Test
    @DisplayName("DELETE /rest/courses/{id} - forbidden without COREDATAADMIN role")
    @WithMockUser(roles = "STAFF")
    void deleteCourse_withoutAdminRole_returns403() throws Exception {
        mockMvc.perform(delete("/rest/courses/1").with(csrf()))
            .andExpect(status().isForbidden());
    }
 */
}
