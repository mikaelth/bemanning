package se.uu.ebc.bemanning.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import se.uu.ebc.bemanning.dto.PhDPositionDTO;
import se.uu.ebc.bemanning.dto.ProgressDTO;
import se.uu.ebc.bemanning.entity.Person;
import se.uu.ebc.bemanning.entity.PhDPosition;
import se.uu.ebc.bemanning.entity.Progress;
import se.uu.ebc.bemanning.entity.OrganisationUnit;
import se.uu.ebc.bemanning.entity.staff.Staff;import se.uu.ebc.bemanning.mapper.PhDPositionMapper;
import se.uu.ebc.bemanning.mapper.ProgressMapper;
import se.uu.ebc.bemanning.repo.PersonRepo;
import se.uu.ebc.bemanning.repo.PhDPositionRepo;
import se.uu.ebc.bemanning.repo.ProgressRepo;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PhDService.
 *
 * Uses plain Mockito (no Spring context) to match the project's service/entity test style.
 * Mockito is configured as a Java agent in pom.xml to avoid self-attaching warnings on JDK 25.
 */
@DisplayName("PhDService Tests")
class PhDServiceTest {

    @Mock
    private PhDPositionMapper phdMapper;

    @Mock
    private ProgressMapper progressMapper;

    @Mock
    private PhDPositionRepo phdPositionRepo;

    @Mock
    private ProgressRepo progressRepo;

    @Mock
    private PersonRepo personRepo;

    @Mock
    private StaffService staffService;

    @Mock
    private Staff staff;

    @InjectMocks
    private PhDService phdService;

    private Person person;
    private PhDPosition phdPosition;
    private PhDPositionDTO phdPositionDTO;
    private Progress progress;
    private ProgressDTO progressDTO;
    private OrganisationUnit organisationUnit;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        person = new Person();
        person.setId(1L);
        person.setGivenName("Anna");
        person.setFamilyName("Lindqvist");

        organisationUnit = new OrganisationUnit();
        organisationUnit.setSvName("Institutionen för biologi");

        when(staff.getOrganisationUnit()).thenReturn(organisationUnit);

        phdPosition = new PhDPosition();
        phdPosition.setId(10L);
        phdPosition.setPerson(person);
        phdPosition.setStart(LocalDateTime.of(2021, 1, 15, 0, 0));
        phdPosition.setNote("Doktorandprojekt");
        phdPosition.setInactive(false);
        phdPosition.setProgresses(new ArrayList<>());

        phdPositionDTO = new PhDPositionDTO();
        phdPositionDTO.setId(10L);
        phdPositionDTO.setPersonId(1L);
        phdPositionDTO.setStart(LocalDate.of(2021, 1, 15));
        phdPositionDTO.setNote("Doktorandprojekt");
        phdPositionDTO.setInactive(false);

        progress = new Progress();
        progress.setId(100L);
        progress.setPhdPosition(phdPosition);
        progress.setDate(LocalDateTime.of(2022, 3, 1, 0, 0));
        progress.setActivity(1.0f);
        progress.setProjectFraction(0.8f);
        progress.setGuFraction(0.2f);
        progress.setRemainingMonths(36.0f);
        progress.setToEcoSys(false);
        progress.setToUpDok(false);

        progressDTO = new ProgressDTO();
        progressDTO.setId(100L);
        progressDTO.setPhdPositionId(10L);
        progressDTO.setDate(LocalDateTime.of(2022, 3, 1, 0, 0));
        progressDTO.setActivity(1.0f);
        progressDTO.setProjectFraction(0.8f);
        progressDTO.setGuFraction(0.2f);
        progressDTO.setRemainingMonths(36.0f);
        progressDTO.setToEcoSys(false);
        progressDTO.setToUpDok(false);
    }


    // -------------------------------------------------------------------------
    // getAllPhDPositions
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getAllPhDPositions - returns mapped DTOs with program name")
    void getAllPhDPositions_returnsMappedDTOs() throws Exception {
        when(phdPositionRepo.findAll()).thenReturn(List.of(phdPosition));
        when(phdMapper.entityToDTO(phdPosition)).thenReturn(phdPositionDTO);
        when(staffService.findUserByPersonAndYear(eq(person), any(String.class))).thenReturn(staff);

        List<PhDPositionDTO> result = phdService.getAllPhDPositions();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Institutionen för biologi", result.get(0).getProgram());
        verify(phdMapper).entityToDTO(phdPosition);
    }

    @Test
    @DisplayName("getAllPhDPositions - sets empty program when staff not found")
    void getAllPhDPositions_emptyProgramWhenNoStaff() throws Exception {
        when(phdPositionRepo.findAll()).thenReturn(List.of(phdPosition));
        when(phdMapper.entityToDTO(phdPosition)).thenReturn(phdPositionDTO);
        when(staffService.findUserByPersonAndYear(eq(person), any(String.class))).thenReturn(null);

        List<PhDPositionDTO> result = phdService.getAllPhDPositions();

        assertEquals(1, result.size());
        assertEquals("", result.get(0).getProgram());
    }

    @Test
    @DisplayName("getAllPhDPositions - returns empty list when no positions exist")
    void getAllPhDPositions_emptyRepo_returnsEmptyList() throws Exception {
        when(phdPositionRepo.findAll()).thenReturn(List.of());

        List<PhDPositionDTO> result = phdService.getAllPhDPositions();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(phdMapper);
    }


    // -------------------------------------------------------------------------
    // getPhDById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getPhDById - returns DTO for existing id")
    void getPhDById_returnsDTO() {
        when(phdPositionRepo.findById(10L)).thenReturn(Optional.of(phdPosition));
        when(phdMapper.entityToDTO(phdPosition)).thenReturn(phdPositionDTO);

        PhDPositionDTO result = phdService.getPhDById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(phdPositionRepo).findById(10L);
        verify(phdMapper).entityToDTO(phdPosition);
    }


    // -------------------------------------------------------------------------
    // savePhDPosition
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("savePhDPosition - creates new position when id is null")
    void savePhDPosition_newPosition_callsDtoToEntity() throws Exception {
        PhDPositionDTO newDTO = new PhDPositionDTO();
        newDTO.setId(null);
        newDTO.setPersonId(1L);
        newDTO.setStart(LocalDate.of(2023, 9, 1));

        when(phdMapper.dtoToEntity(newDTO)).thenReturn(phdPosition);
        when(phdPositionRepo.save(phdPosition)).thenReturn(phdPosition);
        when(phdMapper.entityToDTO(phdPosition)).thenReturn(phdPositionDTO);
        when(staffService.findUserByPersonAndYear(eq(person), any(String.class))).thenReturn(staff);

        PhDPositionDTO result = phdService.savePhDPosition(newDTO);

        assertNotNull(result);
        verify(phdMapper).dtoToEntity(newDTO);
        verify(phdPositionRepo).save(phdPosition);
    }

    @Test
    @DisplayName("savePhDPosition - updates existing position when id is set")
    void savePhDPosition_existingPosition_callsUpdateEntityFromDTO() throws Exception {
        when(phdPositionRepo.findById(10L)).thenReturn(Optional.of(phdPosition));
        doNothing().when(phdMapper).updateEntityFromDTO(eq(phdPositionDTO), eq(phdPosition));
        when(phdPositionRepo.save(phdPosition)).thenReturn(phdPosition);
        when(phdMapper.entityToDTO(phdPosition)).thenReturn(phdPositionDTO);
        when(staffService.findUserByPersonAndYear(eq(person), any(String.class))).thenReturn(staff);

        PhDPositionDTO result = phdService.savePhDPosition(phdPositionDTO);

        assertNotNull(result);
        verify(phdPositionRepo).findById(10L);
        verify(phdMapper).updateEntityFromDTO(phdPositionDTO, phdPosition);
        verify(phdPositionRepo).save(phdPosition);
    }

    @Test
    @DisplayName("savePhDPosition - sets program from staff organisation unit")
    void savePhDPosition_setsProgram() throws Exception {
        when(phdPositionRepo.findById(10L)).thenReturn(Optional.of(phdPosition));
        doNothing().when(phdMapper).updateEntityFromDTO(any(), any());
        when(phdPositionRepo.save(any())).thenReturn(phdPosition);
        when(phdMapper.entityToDTO(phdPosition)).thenReturn(phdPositionDTO);
        when(staffService.findUserByPersonAndYear(eq(person), any(String.class))).thenReturn(staff);

        PhDPositionDTO result = phdService.savePhDPosition(phdPositionDTO);

        assertEquals("Institutionen för biologi", result.getProgram());
    }


    // -------------------------------------------------------------------------
    // deletePhDPosition
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("deletePhDPosition - delegates to repo")
    void deletePhDPosition_callsRepo() {
        doNothing().when(phdPositionRepo).deleteById(10L);

        phdService.deletePhDPosition(10L);

        verify(phdPositionRepo).deleteById(10L);
    }


    // -------------------------------------------------------------------------
    // getAllProgress
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getAllProgress - returns mapped DTOs")
    void getAllProgress_returnsMappedDTOs() throws Exception {
        when(progressRepo.findAll()).thenReturn(List.of(progress));
        when(progressMapper.entityToDTO(progress)).thenReturn(progressDTO);

        List<ProgressDTO> result = phdService.getAllProgress();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getId());
        verify(progressMapper).entityToDTO(progress);
    }

    @Test
    @DisplayName("getAllProgress - returns empty list when no progress entries")
    void getAllProgress_emptyRepo_returnsEmptyList() throws Exception {
        when(progressRepo.findAll()).thenReturn(List.of());

        List<ProgressDTO> result = phdService.getAllProgress();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(progressMapper);
    }


    // -------------------------------------------------------------------------
    // saveProgress
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("saveProgress - creates new progress when id is null")
    void saveProgress_newProgress_createsFromDTO() throws Exception {
        ProgressDTO newDTO = new ProgressDTO();
        newDTO.setId(null);
        newDTO.setPhdPositionId(10L);
        newDTO.setDate(LocalDateTime.of(2023, 6, 1, 0, 0));
        newDTO.setActivity(1.0f);
        newDTO.setProjectFraction(0.8f);
        newDTO.setGuFraction(0.2f);

        doAnswer(inv -> null).when(progressMapper).updateEntityFromDTO(eq(newDTO), any(Progress.class));
        when(progressRepo.save(any(Progress.class))).thenReturn(progress);
        when(progressMapper.entityToDTO(progress)).thenReturn(progressDTO);

        ProgressDTO result = phdService.saveProgress(newDTO);

        assertNotNull(result);
        verify(progressMapper).updateEntityFromDTO(eq(newDTO), any(Progress.class));
        verify(progressRepo).save(any(Progress.class));
    }

    @Test
    @DisplayName("saveProgress - updates existing progress when id is set")
    void saveProgress_existingProgress_updatesFromDTO() throws Exception {
        when(progressRepo.findById(100L)).thenReturn(Optional.of(progress));
        doNothing().when(progressMapper).updateEntityFromDTO(eq(progressDTO), eq(progress));
        when(progressRepo.save(progress)).thenReturn(progress);
        when(progressMapper.entityToDTO(progress)).thenReturn(progressDTO);

        ProgressDTO result = phdService.saveProgress(progressDTO);

        assertNotNull(result);
        verify(progressRepo).findById(100L);
        verify(progressMapper).updateEntityFromDTO(progressDTO, progress);
        verify(progressRepo).save(progress);
    }


    // -------------------------------------------------------------------------
    // deleteProgress
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("deleteProgress - delegates to repo")
    void deleteProgress_callsRepo() {
        doNothing().when(progressRepo).deleteById(100L);

        phdService.deleteProgress(100L);

        verify(progressRepo).deleteById(100L);
    }


    // -------------------------------------------------------------------------
    // allSorted
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("allSorted - returns all positions")
    void allSorted_returnsList() {
        PhDPosition pos2 = new PhDPosition();
        pos2.setId(20L);
        Person person2 = new Person();
        person2.setId(2L);
        person2.setFamilyName("Åberg");
        pos2.setPerson(person2);
        pos2.setStart(LocalDateTime.of(2022, 1, 1, 0, 0));
        pos2.setProgresses(new ArrayList<>());

        person.setFamilyName("Lindqvist");

        when(phdPositionRepo.findAll()).thenReturn(List.of(phdPosition, pos2));
        when(staffService.findUserByPersonAndYear(eq(person), any(String.class))).thenReturn(staff);
        when(staffService.findUserByPersonAndYear(eq(person2), any(String.class))).thenReturn(null);

        List<PhDPosition> result = phdService.allSorted();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("allSorted - returns empty list when no positions")
    void allSorted_emptyRepo_returnsEmptyList() {
        when(phdPositionRepo.findAll()).thenReturn(List.of());

        List<PhDPosition> result = phdService.allSorted();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
