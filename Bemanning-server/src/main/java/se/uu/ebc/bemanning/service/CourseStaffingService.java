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

    @Autowired
    CourseStaffingRepo csRepo;

    @Autowired
    OrganisationUnitRepo ouRepo;

	private ModelMapper mapper = new ModelMapper();
	
	private TypeMap<CourseStaffing, CourseStaffingDTO> csToVOMapper = mapper.createTypeMap(CourseStaffing.class, CourseStaffingDTO.class);
	private TypeMap<CourseStaffingDTO,CourseStaffing> voToCSmapper = mapper.createTypeMap(CourseStaffingDTO.class, CourseStaffing.class);


	@PostConstruct
	public void init () {
 		// csToVOMapper.addMapping(CourseStaffing::currentRemainingProjectTime, CourseStaffingVO::setCurrentRemainingProjectTime);
 		// voToCSmapper.addMapping(CourseStaffing::currentRemainingProjectTime, PhDPositCourseStaffingVOionVO::setCurrentRemainingProjectTime);
	}
 	

	/* CoursesStaffings */
	
	public List<CourseStaffingDTO> getAllCourseStaffings() throws ResourceNotFoundException  {
		List<CourseStaffingDTO> pVO = new ArrayList<CourseStaffingDTO>();
			log.debug("getAllCourseStaffings()");
			for (CourseStaffing p : csRepo.findAll()) {
 				pVO.add(mapper.map(p, CourseStaffingDTO.class));
  			}
         	return pVO;        	        
    }

	public CourseStaffingDTO getCourseStaffingById (Long id) {
		CourseStaffing p = csRepo.findById(id).get();
		return mapper.map(p, CourseStaffingDTO.class);
	}   
		
	public CourseStaffingDTO saveCourseStaffing(CourseStaffingDTO pvo) throws Exception {
    	CourseStaffing p = pvo.getId() == null ? toCourseStaffing(pvo) : toCourseStaffing(csRepo.findById(pvo.getId()).get(), pvo);
    	csRepo.save(p);
 		return mapper.map(p, CourseStaffingDTO.class);
    }

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
