package se.uu.ebc.bemanning.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithUserDetails;

import se.uu.ebc.bemanning.entity.OrganisationUnit;
import se.uu.ebc.bemanning.entity.Person;
import se.uu.ebc.bemanning.entity.staff.AkkaStaff;
import se.uu.ebc.bemanning.entity.staff.ExternalStaff;
import se.uu.ebc.bemanning.entity.staff.Staff;
import se.uu.ebc.bemanning.enums.EmploymentType;
import se.uu.ebc.bemanning.enums.StaffKind;
import se.uu.ebc.bemanning.enums.UserRoleType;
import se.uu.ebc.bemanning.repo.OrganisationUnitRepo;
import se.uu.ebc.bemanning.repo.StaffRepo;
import se.uu.ebc.bemanning.security.UserRepo;
import se.uu.ebc.bemanning.service.StaffService;
import se.uu.ebc.bemanning.vo.StaffVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StaffService Tests")
class StaffServiceTest {

     @Mock
    private StaffRepo staffRepo;

    @Mock
    private OrganisationUnitRepo ouRepo;

    @Mock
    private UserRepo userRepo;

     @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private StaffService staffService;

    private Staff testStaff;
    private StaffVO testStaffVO;
    private Person testPerson;
    private OrganisationUnit testOu;

    @BeforeEach
    void setUp() {
        // Setup test person
        testPerson = new Person();
        testPerson.setId(1L);
        testPerson.setGivenName("John");
        testPerson.setFamilyName("Doe");
        testPerson.setUsername("johndoe");

        // Setup test organisation unit
        testOu = new OrganisationUnit();
        testOu.setId(1L);
        testOu.setAbbreviation("TEST-DEPT");
        testOu.setSvName("Test Department");
        testOu.setEnName("Test Department");
        testOu.setUnitKind("Department");

        // Setup test staff
        testStaff = new AkkaStaff();
        testStaff.setId(1L);
        testStaff.setPerson(testPerson);
        testStaff.setOrganisationUnit(testOu);
        testStaff.setYear("2024");
        testStaff.setPosition(EmploymentType.Lektor);
        testStaff.setPercentGU(0.5f);
        testStaff.setHourlyCharge(500.0f);
        testStaff.setNote("Test note");

        // Setup test StaffVO
        testStaffVO = new StaffVO();
        testStaffVO.setId(1L);
        testStaffVO.setPersonId(1L);
        testStaffVO.setOrganisationUnitId(1L);
        testStaffVO.setYear("2024");
        testStaffVO.setPosition(EmploymentType.Lektor);
        testStaffVO.setPercentGU(0.5f);
        testStaffVO.setHourlyCharge(500.0f);
        testStaffVO.setNote("Test note");
        testStaffVO.setStaffKind(StaffKind.AKKA);
    }

/* 
    @Test
    @DisplayName("Should get all staff successfully")
    void testGetAllStaff() throws ResourceNotFoundException {
        // Arrange
        List<Staff> staffList = Arrays.asList(testStaff);
        when(staffRepo.findAll()).thenReturn(staffList);

        // Act
        List<StaffVO> result = staffService.getAllStaff();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(staffRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no staff exists")
    void testGetAllStaffEmpty() throws ResourceNotFoundException {
        // Arrange
        when(staffRepo.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<StaffVO> result = staffService.getAllStaff();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(staffRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get staff by ID successfully")
    void testGetById() {
        // Arrange
        when(staffRepo.findById(1L)).thenReturn(Optional.of(testStaff));

        // Act
        StaffVO result = staffService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(staffRepo, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should save new AKKA staff successfully")
    void testSaveNewAkkaStaff() throws Exception {
        // Arrange
        testStaffVO.setId(null);
        testStaffVO.setStaffKind(StaffKind.AKKA);
        
        when(staffRepo.save(any(Staff.class))).thenAnswer(invocation -> {
            Staff staff = invocation.getArgument(0);
            staff.setId(1L);
            return staff;
        });

        // Act
        StaffVO result = staffService.saveStaff(testStaffVO);

        // Assert
        assertNotNull(result);
        verify(staffRepo, times(1)).save(any(AkkaStaff.class));
    }

    @Test
    @DisplayName("Should save new EXTERNAL staff successfully")
    void testSaveNewExternalStaff() throws Exception {
        // Arrange
        testStaffVO.setId(null);
        testStaffVO.setStaffKind(StaffKind.EXTERNAL);
        
        when(staffRepo.save(any(Staff.class))).thenAnswer(invocation -> {
            Staff staff = invocation.getArgument(0);
            staff.setId(1L);
            return staff;
        });

        // Act
        StaffVO result = staffService.saveStaff(testStaffVO);

        // Assert
        assertNotNull(result);
        verify(staffRepo, times(1)).save(any(ExternalStaff.class));
    }

    @Test
    @DisplayName("Should update existing staff successfully")
    void testUpdateExistingStaff() throws Exception {
        // Arrange
        testStaffVO.setId(1L);
        when(staffRepo.findById(1L)).thenReturn(Optional.of(testStaff));
        when(staffRepo.save(any(Staff.class))).thenReturn(testStaff);

        // Act
        StaffVO result = staffService.saveStaff(testStaffVO);

        // Assert
        assertNotNull(result);
        verify(staffRepo, times(1)).findById(1L);
        verify(staffRepo, times(1)).save(any(Staff.class));
    }

    @Test
    @DisplayName("Should throw exception for invalid staff kind")
    void testSaveStaffInvalidKind() {
        // Arrange
        testStaffVO.setId(null);
        testStaffVO.setStaffKind(null);

        // Act & Assert
        assertThrows(Exception.class, () -> staffService.saveStaff(testStaffVO));
    }

    @Test
    @DisplayName("Should delete staff successfully")
    void testDeleteStaff() {
        // Arrange
        doNothing().when(staffRepo).deleteById(1L);

        // Act
        assertDoesNotThrow(() -> staffService.deleteStaff(1L));

        // Assert
        verify(staffRepo, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent staff")
    void testDeleteNonExistentStaff() {
        // Arrange
        doThrow(new IllegalArgumentException("Staff not found"))
            .when(staffRepo).deleteById(999L);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> staffService.deleteStaff(999L));
    }

    @Test
    @DisplayName("Should find user by person and year")
    void testFindUserByPersonAndYear() {
        // Arrange
        List<Staff> staffList = Arrays.asList(testStaff);
        when(staffRepo.findUserByPersonAndYear(testPerson, "2024")).thenReturn(staffList);

        // Act
        Staff result = staffService.findUserByPersonAndYear(testPerson, "2024");

        // Assert
        assertNotNull(result);
        assertEquals(testStaff, result);
        verify(staffRepo, times(1)).findUserByPersonAndYear(testPerson, "2024");
    }

    @Test
    @DisplayName("Should return null when no staff found for person and year")
    void testFindUserByPersonAndYearNotFound() {
        // Arrange
        when(staffRepo.findUserByPersonAndYear(testPerson, "2024")).thenReturn(new ArrayList<>());

        // Act
        Staff result = staffService.findUserByPersonAndYear(testPerson, "2024");

        // Assert
        assertNull(result);
    }
 

    @Test
    @DisplayName("Should get assigned staff by year and department")
    @WithUserDetails("customUsername")
    void testGetAssignedStaffByYearAndDept() {
        // Arrange
        List<OrganisationUnit> expandedOu = Arrays.asList(testOu);
        when(testOu.getExpandedOu("2024")).thenReturn(expandedOu);
        
        List<Staff> staffList = Arrays.asList(testStaff);
        when(staffRepo.findUserByOuListAndYear(expandedOu, "2024")).thenReturn(staffList);

        // Act
        List<Staff> result = staffService.getAssignedStaff("2024", testOu);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testStaff, result.get(0));
    }
*/
    @Test
    @DisplayName("Should get all staff for CoreDataAdmin role")
     @WithUserDetails("mikathol")
   void testGetAssignedStaffForCoreDataAdmin() {
        // Arrange
        setupSecurityContext("admin", UserRoleType.CoreDataAdmin);
        
        List<Staff> staffList = Arrays.asList(testStaff);
        when(staffRepo.findByYear("2024")).thenReturn(staffList);

        // Act
        List<Staff> result = staffService.getAssignedStaff("2024");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(staffRepo, times(1)).findByYear("2024");
    }

    @Test
    @DisplayName("Should get department staff for DirectorOfStudies role")
    void testGetAssignedStaffForDirectorOfStudies() {
        // Arrange
        setupSecurityContext("johndoe", UserRoleType.DirectorOfStudies);
        
        when(userRepo.findUserByUsername("johndoe")).thenReturn(testPerson);
        
        List<Staff> userStaffList = Arrays.asList(testStaff);
        when(staffRepo.findUserByPersonAndYear(testPerson, "2024")).thenReturn(userStaffList);
        
        OrganisationUnit economyHolder = new OrganisationUnit();
        economyHolder.setId(2L);
        economyHolder.setAbbreviation("ECON-DEPT");
        economyHolder.setSvName("Economy Department");
        economyHolder.setEnName("Economy Department");
        economyHolder.setUnitKind("Department");
        
        when(testStaff.getOrganisationUnit()).thenReturn(testOu);
        when(testOu.getEconomyHolder("2024")).thenReturn(economyHolder);
        
        List<OrganisationUnit> expandedOu = Arrays.asList(economyHolder);
        when(economyHolder.getExpandedOu("2024")).thenReturn(expandedOu);
        
        List<Staff> deptStaffList = Arrays.asList(testStaff);
        when(staffRepo.findUserByOuListAndYear(expandedOu, "2024")).thenReturn(deptStaffList);

        // Act
        List<Staff> result = staffService.getAssignedStaff("2024");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should get own staff for regular user")
    void testGetAssignedStaffForRegularUser() {
        // Arrange
        setupSecurityContext("johndoe", UserRoleType.Staff);
        
        when(userRepo.findUserByUsername("johndoe")).thenReturn(testPerson);
        
        List<Staff> staffList = Arrays.asList(testStaff);
        when(staffRepo.findUserByPersonAndYear(testPerson, "2024")).thenReturn(staffList);

        // Act
        List<Staff> result = staffService.getAssignedStaff("2024");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(staffRepo, times(1)).findUserByPersonAndYear(testPerson, "2024");
    }

    @Test
    @DisplayName("Should return empty list when authentication is null")
    void testGetAssignedStaffNoAuthentication() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        List<Staff> result = staffService.getAssignedStaff("2024");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when user is not authenticated")
    void testGetAssignedStaffNotAuthenticated() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // Act
        List<Staff> result = staffService.getAssignedStaff("2024");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // Helper method to setup security context
    private void setupSecurityContext(String username, UserRoleType role) {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(username);
        
        Collection<GrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority(role.toString().toUpperCase())
        );
        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);
    }
}
