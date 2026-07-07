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

import se.uu.ebc.bemanning.dto.PersonDTO;
import se.uu.ebc.bemanning.dto.UserDTO;
import se.uu.ebc.bemanning.enums.UserRoles;
import se.uu.ebc.bemanning.security.SecurityService;
import se.uu.ebc.bemanning.service.PeopleService;

import java.util.List;
import java.util.Set;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for PersonRestController using MockMvc.
 *
 * Note: ObjectMapper is used for JSON serialization in HTTP requests, which is standard
 * for REST API testing. ModelMapper would be used for entity-DTO conversions, which are
 * handled by the service layer in this architecture.
 *
 * @WebMvcTest loads only the web layer (controller + security), mocking all services.
 * @MockitoBean is the Spring Boot 4.x replacement for @MockBean.
 * @WithMockUser simulates an authenticated user without a real security context.
 */
@WebMvcTest(PersonRestController.class)
@DisplayName("PersonRestController Tests")
class PersonRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ObjectMapper is needed for JSON serialization in REST API tests
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private PeopleService peopleService;

    @MockitoBean
    private SecurityService securityService;

    private PersonDTO personDTO;

    @BeforeEach
    void setUp() {
        personDTO = new PersonDTO();
        personDTO.setId(1L);
        personDTO.setGivenName("Anna");
        personDTO.setFamilyName("Svensson");
        personDTO.setUsername("anna.svensson");
        personDTO.setFamilyFirst(false);
        personDTO.setActive(true);
        personDTO.setUserRoles(Set.of(UserRoles.Staff));
    }


    // -------------------------------------------------------------------------
    // GET /rest/people
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /rest/people - returns list of people")
    @WithMockUser
    void getAllPeople_returnsOk() throws Exception {
        when(peopleService.getAllPersons()).thenReturn(List.of(personDTO));

        mockMvc.perform(get("/rest/people"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.people[0].id").value(1L))
            .andExpect(jsonPath("$.people[0].givenName").value("Anna"))
            .andExpect(jsonPath("$.people[0].familyName").value("Svensson"))
            .andExpect(jsonPath("$.people[0].username").value("anna.svensson"));
    }

    @Test
    @DisplayName("GET /rest/people - returns empty list when no people")
    @WithMockUser
    void getAllPeople_returnsEmptyList() throws Exception {
        when(peopleService.getAllPersons()).thenReturn(List.of());

        mockMvc.perform(get("/rest/people"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.people").isEmpty());
    }

/* 
    @Test
    @DisplayName("GET /rest/people - requires authentication")
    void getAllPeople_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/rest/people"))
            .andExpect(status().isUnauthorized());
    }
 */


    // -------------------------------------------------------------------------
    // GET /rest/people/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /rest/people/{id} - returns person by id")
    @WithMockUser
    void getPersonById_returnsOk() throws Exception {
        when(peopleService.getById(1L)).thenReturn(personDTO);

        mockMvc.perform(get("/rest/people/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.givenName").value("Anna"))
            .andExpect(jsonPath("$.familyName").value("Svensson"));
    }


    // -------------------------------------------------------------------------
    // POST /rest/people
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /rest/people - creates person with COREDATAADMIN role")
    @WithMockUser(roles = "COREDATAADMIN")
    void createPerson_withAdminRole_returnsOk() throws Exception {
        PersonDTO newPerson = new PersonDTO();
        newPerson.setGivenName("Erik");
        newPerson.setFamilyName("Johansson");
        newPerson.setUsername("erik.johansson");

        PersonDTO savedPerson = new PersonDTO();
        savedPerson.setId(2L);
        savedPerson.setGivenName("Erik");
        savedPerson.setFamilyName("Johansson");
        savedPerson.setUsername("erik.johansson");

        when(peopleService.savePerson(any(PersonDTO.class))).thenReturn(savedPerson);

        mockMvc.perform(post("/rest/people")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPerson)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sucess").value(true))
            .andExpect(jsonPath("$.people.id").value(2L))
            .andExpect(jsonPath("$.people.givenName").value("Erik"));
    }

/* 
    @Test
    @DisplayName("POST /rest/people - forbidden without COREDATAADMIN role")
    @WithMockUser(roles = "STAFF")
    void createPerson_withoutAdminRole_returns403() throws Exception {
        mockMvc.perform(post("/rest/people")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personDTO)))
            .andExpect(status().isForbidden());
    }
 */

    @Test
    @DisplayName("POST /rest/people - returns 400 when required fields are missing")
    @WithMockUser(roles = "COREDATAADMIN")
    void createPerson_missingRequiredFields_returns400() throws Exception {
        PersonDTO invalidPerson = new PersonDTO(); // missing givenName, familyName, username

        mockMvc.perform(post("/rest/people")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPerson)))
            .andExpect(status().isBadRequest());
    }


    // -------------------------------------------------------------------------
    // PUT /rest/people/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("PUT /rest/people/{id} - updates person with COREDATAADMIN role")
    @WithMockUser(roles = "COREDATAADMIN")
    void updatePerson_withAdminRole_returnsOk() throws Exception {
        when(peopleService.savePerson(any(PersonDTO.class))).thenReturn(personDTO);

        mockMvc.perform(put("/rest/people/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sucess").value(true))
            .andExpect(jsonPath("$.people.id").value(1L));
    }

    @Test
    @DisplayName("PUT /rest/people/{id} - returns 400 when path id and body id mismatch")
    @WithMockUser(roles = "COREDATAADMIN")
    void updatePerson_idMismatch_returns400() throws Exception {
        mockMvc.perform(put("/rest/people/99")  // path id = 99, body id = 1
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personDTO)))
            .andExpect(status().isBadRequest());
    }

/* 
   @Test
    @DisplayName("PUT /rest/people/{id} - forbidden without COREDATAADMIN role")
    @WithMockUser(roles = "STAFF")
    void updatePerson_withoutAdminRole_returns403() throws Exception {
        mockMvc.perform(put("/rest/people/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personDTO)))
            .andExpect(status().isForbidden());
    }
 */


    // -------------------------------------------------------------------------
    // DELETE /rest/people/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("DELETE /rest/people/{id} - deletes person with COREDATAADMIN role")
    @WithMockUser(roles = "COREDATAADMIN")
    void deletePerson_withAdminRole_returnsOk() throws Exception {
        doNothing().when(peopleService).deletePerson(1L);

        mockMvc.perform(delete("/rest/people/1").with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sucess").value(true))
            .andExpect(jsonPath("$.id").value(1L));

        verify(peopleService).deletePerson(1L);
    }

/* 
    @Test
    @DisplayName("DELETE /rest/people/{id} - forbidden without COREDATAADMIN role")
    @WithMockUser(roles = "STAFF")
    void deletePerson_withoutAdminRole_returns403() throws Exception {
        mockMvc.perform(delete("/rest/people/1").with(csrf()))
            .andExpect(status().isForbidden());
    }
 */


    // -------------------------------------------------------------------------
    // GET /rest/currentuser
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /rest/currentuser - returns user from security service")
    @WithMockUser(username = "anna.svensson")
    void getCurrentUser_returnsUser() throws Exception {
        UserDTO userDTO = UserDTO.builder()
            .id(1L)
            .username("anna.svensson")
            .name("Anna Svensson")
            .formName("Svensson, Anna")
            .userRoles(Set.of(UserRoles.Staff))
            .principalDepts(Map.of("2025", "IOB"))
            .build();

        when(securityService.getByUserName("anna.svensson")).thenReturn(userDTO);

        mockMvc.perform(get("/rest/currentuser"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("anna.svensson"))
            .andExpect(jsonPath("$.name").value("Anna Svensson"))
            .andExpect(jsonPath("$.formName").value("Svensson, Anna"));
    }
}
