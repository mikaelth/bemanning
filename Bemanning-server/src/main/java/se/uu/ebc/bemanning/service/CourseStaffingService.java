package se.uu.ebc.bemanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import jakarta.annotation.PostConstruct;

import org.springframework.dao.OptimisticLockingFailureException;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import se.uu.ebc.bemanning.dto.CourseStaffingDTO;
import se.uu.ebc.bemanning.dto.PhDPositionDTO;
import se.uu.ebc.bemanning.entity.PhDPosition;
import se.uu.ebc.bemanning.entity.assignment.CourseStaffing;
import se.uu.ebc.bemanning.entity.assignment.CourseStaffingLegacy;
import se.uu.ebc.bemanning.entity.assignment.CourseStaffingModern;
import se.uu.ebc.bemanning.entity.courseinstance.CourseInstance;
import se.uu.ebc.bemanning.entity.staff.Staff;
import se.uu.ebc.bemanning.enums.CourseStaffingType;
import se.uu.ebc.bemanning.repo.CourseStaffingRepo;
import se.uu.ebc.bemanning.repo.OrganisationUnitRepo;


import lombok.extern.slf4j.Slf4j;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;

@Slf4j
@Service
public class CourseStaffingService {

//    @Autowired
    CourseStaffingRepo csRepo;

//    @Autowired
    OrganisationUnitRepo ouRepo;

// 	private ModelMapper mapper = new ModelMapper();
// 	
// 	private TypeMap<CourseStaffing, CourseStaffingDTO> csToVOMapper = mapper.createTypeMap(CourseStaffing.class, CourseStaffingDTO.class);
// 	private TypeMap<CourseStaffingDTO,CourseStaffing> voToCSmapper = mapper.createTypeMap(CourseStaffingDTO.class, CourseStaffing.class);
// 

	public CourseStaffingService (CourseStaffingRepo csRepo, OrganisationUnitRepo ouRepo) {
		this.csRepo = csRepo;
		this.ouRepo = ouRepo;
	}
	
	@PostConstruct
	public void init () {
 		// csToVOMapper.addMapping(CourseStaffing::currentRemainingProjectTime, CourseStaffingVO::setCurrentRemainingProjectTime);
 		// voToCSmapper.addMapping(CourseStaffing::currentRemainingProjectTime, PhDPositCourseStaffingVOionVO::setCurrentRemainingProjectTime);
	}
 	

	/* CoursesStaffings */
	
	public List<CourseStaffing> getAllCourseStaffings() throws ResourceNotFoundException  {
		List<CourseStaffing> pVO = csRepo.findAll();
    	return pVO;        	        
    }

	public List<CourseStaffing> getCourseStaffingsByYear(String year) throws ResourceNotFoundException  {
		List<CourseStaffing> pVO = csRepo.findCourseStaffingByYear(year);
    	return pVO;        	        
    }

	public CourseStaffing getCourseStaffingById (Long id) {
		CourseStaffing p = csRepo.findById(id).orElseThrow();
		return p;
	}   
		
	public CourseStaffing saveCourseStaffing(CourseStaffing p) throws Exception {
    	csRepo.save(p);
 		return p;
    }

/* 
	private CourseStaffing toCourseStaffing (CourseStaffingDTO pvo) throws Exception {
		CourseStaffing cs = switch (pvo.getType()) {
			case null -> throw new IllegalArgumentException();
			case CourseStaffingType.LEGACY -> new CourseStaffingLegacy();
			case CourseStaffingType.MODERN -> new CourseStaffingModern();
			default -> new CourseStaffingModern();
		};
		return toCourseStaffing (cs, pvo);
   	}

	private CourseStaffing toCourseStaffing (CourseStaffing p, CourseStaffingDTO pvo) throws Exception {
		mapper.map(pvo, p);
		return p;
	}
 */
    
	public synchronized void deleteCourseStaffing(Long pID) throws IllegalArgumentException, OptimisticLockingFailureException {
		csRepo.deleteById(pID);
		return;
    }


	public CourseStaffing createCouurseStaffing (Staff staff, CourseInstance ci) {
		CourseStaffingModern cs = new CourseStaffingModern();
		cs.setStaff(staff);
		cs.setCourseInstance(ci);
		cs.setAssigningDept(ouRepo.findByAbbreviation("IBG"));
		
		return cs;
	}

}
