package se.uu.ebc.bemanning.controller;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import lombok.extern.slf4j.Slf4j;

import se.uu.ebc.bemanning.repo.CourseStaffingRepo;
import se.uu.ebc.bemanning.dto.CourseStaffingDTO;
import se.uu.ebc.bemanning.dto.StaffDTO;
import se.uu.ebc.bemanning.entity.staff.Staff;
import se.uu.ebc.bemanning.enums.UserRoles;
import se.uu.ebc.bemanning.service.CourseStaffingService;

import java.util.List;
import java.util.HashSet;
import java.util.HashMap;
import java.time.Year;

@RestController
@RequestMapping(value = "/rest")
@CrossOrigin(origins = {"http://localhost:1962","http://localhost:1841"})
@Slf4j
public class CoursStaffingRestController {


	@Autowired
	CourseStaffingService csService;


	private record DeleteStatus (Boolean sucess, Long id) {}
	private record CreateStaffingStatus (Boolean sucess, CourseStaffingDTO staffing) {}
	private record CreateStaffingListStatus (Boolean sucess, List<CourseStaffingDTO> staffing) {}

	/* Persons */
		
    @GetMapping(value="/coursestaffing")
    public ResponseEntity<CreateStaffingListStatus> getAllEntities() {
		return ResponseEntity.ok(new CreateStaffingListStatus (true, csService.getAllCourseStaffings() )); 
    }

    @GetMapping(value="/coursestaffing/{id}")
    public CourseStaffingDTO getEntity(@PathVariable Long id) {
		return csService.getCourseStaffingById(id); 
    }
   
	@PreAuthorize("hasRole('ROLE_DIRECTOROFSTUDIES')")
    @PostMapping(value="/coursestaffing")
	public ResponseEntity<CreateStaffingStatus> createEntity(@RequestBody CourseStaffingDTO sVO) throws Exception {
		CourseStaffingDTO nsVO = csService.saveCourseStaffing(sVO);
		return ResponseEntity.ok(new CreateStaffingStatus(true,nsVO));
	}

	@PreAuthorize("hasRole('ROLE_DIRECTOROFSTUDIES')")
    @PutMapping(value="/coursestaffing/{id}")
    public ResponseEntity<CreateStaffingStatus> updateEntity(@RequestBody CourseStaffingDTO sVO, @PathVariable Long id) throws Exception {
		if (sVO.getId().equals(id)) {
			CourseStaffingDTO nsVO = csService.saveCourseStaffing(sVO);
			return ResponseEntity.ok(new CreateStaffingStatus(true,nsVO));		
		} else {
			throw ( new IllegalArgumentException() );
		} 			
    }

	@PreAuthorize("hasRole('ROLE_DIRECTOROFSTUDIES')")
	@DeleteMapping(value = "/coursestaffing/{id}")
	public ResponseEntity deleteEntity(@PathVariable Long id) {
		csService.deleteCourseStaffing(id);
		return ResponseEntity.ok(new DeleteStatus(true,id));
    }

 	
} 
