package se.uu.ebc.bemanning.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import se.uu.ebc.bemanning.entity.staff.Staff;
import se.uu.ebc.bemanning.enums.EmploymentType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PhDPosition entity.
 * 
 * Note: Mockito is configured as a Java agent in pom.xml (maven-surefire-plugin)
 * to avoid self-attaching warnings in newer JDK versions.
 */
 
@DisplayName("PhDPosition Entity Tests")
class PhDPositionTest {

    private PhDPosition phdPosition;
    
    @Mock
    private Person mockPerson;
    
    @Mock
    private Staff mockStaff;

/* 
	@Mock 
	private Progress p1;
	@Mock 
	private Progress p2;
	@Mock 
	private Progress p3;
	@Mock 
	private Progress p4;
 */
	
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        phdPosition = new PhDPosition();
        phdPosition.setId(1L);
        phdPosition.setPerson(mockPerson);
        phdPosition.setStart(LocalDateTime.of(2020, 1, 1, 0, 0));
        phdPosition.setDissertation(LocalDateTime.of(2024, 12, 31, 0, 0));
        phdPosition.setNote("Test PhD Position");
        phdPosition.setInactive(false);
        phdPosition.setProgresses(new ArrayList<Progress>());
    }

    @Test
    @DisplayName("Should create PhDPosition with all fields")
    void testPhDPositionCreation() {
        assertNotNull(phdPosition);
        assertEquals(1L, phdPosition.getId());
        assertEquals(mockPerson, phdPosition.getPerson());
        assertEquals(LocalDateTime.of(2020, 1, 1, 0, 0), phdPosition.getStart());
        assertEquals(LocalDateTime.of(2024, 12, 31, 0, 0), phdPosition.getDissertation());
        assertEquals("Test PhD Position", phdPosition.getNote());
        assertFalse(phdPosition.isInactive());
    }

    @Test
    @DisplayName("Should return start date when no progresses exist")
    void testGetStartWithNoProgresses() {
        LocalDateTime expectedStart = LocalDateTime.of(2020, 1, 1, 0, 0);
        assertEquals(expectedStart, phdPosition.getStart());
    }

    @Test
    @DisplayName("Should return latest progress date when progresses exist")
    void testGetStartWithProgresses() {
        Progress progress1 = createProgress(LocalDateTime.of(2020, 6, 1, 0, 0), 1.0f, 0.8f, 0.2f);
        Progress progress2 = createFullProgress(LocalDateTime.of(2020, 3, 1, 0, 0), 1.0f, 0.8f, 0.2f, 48.0f,0.0f);
        
        List<Progress> progresses = new ArrayList<>();
        progresses.add(progress1);
        progresses.add(progress2);
        phdPosition.setProgresses(progresses);
        
        // Should return the earliest progress date (last in sorted list)
        assertEquals(LocalDateTime.of(2020, 3, 1, 0, 0), phdPosition.getStart());
    }

    @Test
    @DisplayName("Should sort progresses in descending order")
    void testGetProgressesSorting() {
        Progress progress1 = createProgress(LocalDateTime.of(2020, 3, 1, 0, 0), 1.0f, 0.8f, 0.2f);
        Progress progress2 = createProgress(LocalDateTime.of(2020, 6, 1, 0, 0), 1.0f, 0.8f, 0.2f);
        Progress progress3 = createFullProgress(LocalDateTime.of(2020, 1, 1, 0, 0), 1.0f, 0.8f, 0.2f, 48.0f, 0.0f);
        
        List<Progress> progresses = new ArrayList<>();
        progresses.add(progress1);
        progresses.add(progress2);
        progresses.add(progress3);
        phdPosition.setProgresses(progresses);
        
        List<Progress> sortedProgresses = phdPosition.getProgresses();
        
        // Should be sorted in descending order (newest first)
        assertEquals(LocalDateTime.of(2020, 6, 1, 0, 0), sortedProgresses.get(0).getDate());
        assertEquals(LocalDateTime.of(2020, 3, 1, 0, 0), sortedProgresses.get(1).getDate());
        assertEquals(LocalDateTime.of(2020, 1, 1, 0, 0), sortedProgresses.get(2).getDate());
    }

    @Test
    @DisplayName("Should return current time when no progresses for predicted finish date")
    void testPredictedFinishDateWithNoProgresses() {


        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        LocalDateTime result = phdPosition.predictedFinishDate();
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);
        
        assertTrue(result.isAfter(before) && result.isBefore(after));
    }

    @Test
    @DisplayName("Should calculate predicted finish date with progresses")
    void testPredictedFinishDateWithProgresses() {
        Progress progress1 = createProgress(LocalDateTime.of(2022, 3, 1, 0, 0), 1.0f, 0.8f, 0.2f);
        Progress progress2 = createProgress(LocalDateTime.of(2021, 6, 1, 0, 0), 1.0f, 0.8f, 0.2f);
        Progress progress3 = createFullProgress(LocalDateTime.of(2020, 1, 1, 0, 0), 1.0f, 0.8f, 0.2f, 48.0f, 0.0f);
        
        List<Progress> progresses = new ArrayList<>();
        progresses.add(progress1);
        progresses.add(progress2);
        progresses.add(progress3);
        phdPosition.setProgresses(progresses);
                
        LocalDateTime result = phdPosition.predictedFinishDate();
        
        assertNotNull(result);
        assertTrue(result.isAfter(progress3.getDate()));
    }

/* 
    @Test
    @DisplayName("Should calculate predicted half time")
    void testPredictedHalfTime() {
        Progress progress1 = createProgress(LocalDateTime.of(2022, 3, 1, 0, 0), 1.0f, 0.8f, 0.2f);
        Progress progress2 = createProgress(LocalDateTime.of(2021, 12, 15, 0, 0), 1.0f, 0.7f, 0.2f);
        Progress progress3 = createProgress(LocalDateTime.of(2021, 6, 1, 0, 0), 1.0f, 0.7f, 0.2f);
        Progress progress4 = createFullProgress(LocalDateTime.of(2020, 1, 1, 0, 0), 1.0f, 0.8f, 0.2f, 48.0f, 0.0f);
        
        List<Progress> progresses = new ArrayList<>();
        progresses.add(progress1);
        progresses.add(progress2);
        progresses.add(progress3);
        progresses.add(progress4);
        phdPosition.setProgresses(progresses);
        
        LocalDateTime result = phdPosition.predictedHalfTime();
        
        assertNotNull(result);
    }
 */

    @Test
    @DisplayName("Should calculate predicted 80 percent")
    void testPredicted80Percent() {
        Progress progress1 = createProgress(LocalDateTime.of(2022, 3, 1, 0, 0), 1.0f, 0.8f, 0.2f);
        Progress progress2 = createProgress(LocalDateTime.of(2021, 6, 1, 0, 0), 1.0f, 0.8f, 0.2f);
        Progress progress3 = createFullProgress(LocalDateTime.of(2020, 1, 1, 0, 0), 1.0f, 0.8f, 0.2f, 48.0f, 0.0f);
        
        List<Progress> progresses = new ArrayList<>();
        progresses.add(progress1);
        progresses.add(progress2);
        progresses.add(progress3);
        phdPosition.setProgresses(progresses);
        
        LocalDateTime result = phdPosition.predicted80Percent();
        
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should calculate predicted 75 percent")
    void testPredicted75Percent() {
        Progress progress1 = createProgress(LocalDateTime.of(2022, 3, 1, 0, 0), 1.0f, 0.8f, 0.2f);
        Progress progress2 = createProgress(LocalDateTime.of(2021, 6, 1, 0, 0), 1.0f, 0.8f, 0.2f);
        Progress progress3 = createFullProgress(LocalDateTime.of(2020, 1, 1, 0, 0), 1.0f, 0.8f, 0.2f, 48.0f, 0.0f);
        
        List<Progress> progresses = new ArrayList<>();
        progresses.add(progress1);
        progresses.add(progress2);
        progresses.add(progress3);
        phdPosition.setProgresses(progresses);
        
        LocalDateTime result = phdPosition.predicted75Percent();
        
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should calculate remaining project time")
    void testRemainingProjectTime() {
        Progress progress = createProgress(LocalDateTime.of(2020, 6, 1, 0, 0), 1.0f, 0.8f, 0.2f);
        
        List<Progress> progresses = new ArrayList<>();
        progresses.add(progress);
        phdPosition.setProgresses(progresses);
        
        LocalDateTime testDate = LocalDateTime.of(2021, 1, 1, 0, 0);
        Float result = phdPosition.remainingProjectTime(testDate, false);
        
        assertNotNull(result);
        assertTrue(result >= 0.0f);
    }

    @Test
    @DisplayName("Should calculate current remaining project time")
    void testCurrentRemainingProjectTime() {
        Progress progress = createProgress(LocalDateTime.of(2020, 6, 1, 0, 0), 1.0f, 0.8f, 0.2f);
        
        List<Progress> progresses = new ArrayList<>();
        progresses.add(progress);
        phdPosition.setProgresses(progresses);
        
        Float result = phdPosition.currentRemainingProjectTime();
        
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should calculate yearly GU")
    void testYearlyGU() {
        Progress progress1 = createProgress(LocalDateTime.of(2020, 1, 15, 0, 0), 1.0f, 0.8f, 0.5f);
        Progress progress2 = createProgress(LocalDateTime.of(2020, 6, 1, 0, 0), 1.0f, 0.8f, 0.3f);
        
        List<Progress> progresses = new ArrayList<>();
        progresses.add(progress1);
        progresses.add(progress2);
        phdPosition.setProgresses(progresses);
        
        Float result = phdPosition.yearlyGU("2020");
        
        assertNotNull(result);
        assertTrue(result >= 0.0f && result <= 1.0f);
    }

    @Test
    @DisplayName("Should calculate used ISP date")
    void testUsedISPDate() {
        // Setup mock person with staff
        when(mockPerson.getName()).thenReturn("Test Person");
        
        Set<Staff> staffSet = new HashSet<>();
        staffSet.add(mockStaff);
        when(mockPerson.getStaff()).thenReturn(staffSet);
        
        when(mockStaff.getYear()).thenReturn("2020");
        when(mockStaff.getPosition()).thenReturn(EmploymentType.Doktorand);
        when(mockStaff.getTotalHours(any(LocalDateTime.class))).thenReturn(100.0f);
        when(mockStaff.getIb()).thenReturn(10.0f);
        
		Progress progress1 = createProgress(LocalDateTime.of(2022, 3, 1, 0, 0), 1.0f, 0.8f, 0.2f);
        Progress progress2 = createProgress(LocalDateTime.of(2021, 6, 1, 0, 0), 1.0f, 0.8f, 0.2f);
        Progress progress3 = createFullProgress(LocalDateTime.of(2020, 1, 1, 0, 0), 1.0f, 0.8f, 0.2f, 48.0f, 0.0f);
        
        List<Progress> progresses = new ArrayList<>();
        progresses.add(progress1);
        progresses.add(progress2);
        progresses.add(progress3);
        phdPosition.setProgresses(progresses);
        
        LocalDateTime ispDate = LocalDateTime.of(2020, 6, 1, 0, 0);
        Float result = phdPosition.usedISPDate(ispDate);
        
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should handle no-args constructor")
    void testNoArgsConstructor() {
        PhDPosition newPosition = new PhDPosition();
        assertNotNull(newPosition);
        assertNotNull(newPosition.getProgresses());
        assertTrue(newPosition.getProgresses().isEmpty());
    }

    @Test
    @DisplayName("Should handle all-args constructor")
    void testAllArgsConstructor() {
        LocalDateTime start = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime dissertation = LocalDateTime.of(2024, 12, 31, 0, 0);
        List<Progress> progresses = new ArrayList<>();
        
        PhDPosition position = new PhDPosition(
            1L,
            mockPerson,
            progresses,
            start,
            dissertation,
            "Test Note",
            false
        );
        
        assertNotNull(position);
        assertEquals(1L, position.getId());
        assertEquals(mockPerson, position.getPerson());
        assertEquals(start, position.getStart());
        assertEquals(dissertation, position.getDissertation());
        assertEquals("Test Note", position.getNote());
        assertFalse(position.isInactive());
    }

    // Helper methods to create Progress objects
    private Progress createFullProgress(LocalDateTime date, Float activity, Float projectFraction, Float guFraction, Float remainingMonths, Float addedMonths) {
    	Progress p = createProgress( date,  activity,  projectFraction,  guFraction);
        p.setRemainingMonths(remainingMonths);
        p.setAddedMonths(addedMonths);        
        return p;
    }
    private Progress createProgress(LocalDateTime date, Float activity, Float projectFraction, Float guFraction) {
        Progress progress = new Progress();
        progress.setDate(date);
        progress.setActivity(activity);
        progress.setProjectFraction(projectFraction);
        progress.setGuFraction(guFraction);
        progress.setPhdPosition(phdPosition);
        return progress;
    }
}
