package se.uu.ebc.bemanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;


import se.uu.ebc.bemanning.entity.CourseInstance;
import se.uu.ebc.bemanning.entity.OrganisationUnit;
import se.uu.ebc.bemanning.entity.Person;
import se.uu.ebc.bemanning.entity.Staff;
import se.uu.ebc.bemanning.entity.Assignment;
import se.uu.ebc.bemanning.security.UserRepo;
import se.uu.ebc.bemanning.repo.AssignmentRepo;
import se.uu.ebc.bemanning.repo.StaffRepo;
import se.uu.ebc.bemanning.repo.CourseInstanceRepo;
import se.uu.ebc.bemanning.repo.OrganisationUnitRepo;
import se.uu.ebc.bemanning.vo.AssignmentVO;

import org.apache.log4j.Logger;

@Service
public class AssignmentService {

    private static Logger logger = Logger.getLogger(AssignmentService.class.getName());


	@Autowired
	StaffRepo staffRepo;

	@Autowired
	CourseInstanceRepo ciRepo;

	@Autowired
	AssignmentRepo asmRepo;

	@Autowired
	OrganisationUnitRepo ouRepo;

	public List<AssignmentVO> getAllAssignments() throws Exception {
		List<AssignmentVO> aVO = new ArrayList<AssignmentVO>();
		try {	
			for (Assignment a : asmRepo.findAll()) {
 				aVO.add(new AssignmentVO(a));
 			}
         	return aVO;        	        
        } catch (Exception e) {

			return null;
			
        }
    }
    
    public AssignmentVO saveAssignment(AssignmentVO avo) throws Exception {
    	Assignment a = avo.getId() == null ? toAssignment(avo) : toAssignment(asmRepo.findById(avo.getId()), avo);
    	asmRepo.save(a);
		return new AssignmentVO(a);
    
    }


    public synchronized void deleteAssignment(Long id) throws Exception {
		Assignment a = asmRepo.findById(id);
		asmRepo.delete(a);
    }

   	
 
	private Assignment toAssignment (AssignmentVO avo) throws Exception {
 		return toAssignment (new Assignment(), avo);
   	}

	private Assignment toAssignment (Assignment entity, AssignmentVO vo) throws Exception {


		try {
			entity.setId(vo.getId());

			entity.setStaff(staffRepo.findById(vo.getStaffId()));
			entity.setCourseInstance(ciRepo.findById(vo.getCourseInstanceId()));

			entity.setAssigningDept(ouRepo.findByAbbreviation(vo.getAssigningDept()));

			entity.setHoursAdmin(vo.getHoursAdmin());
			entity.setHoursDevelopment(vo.getHoursDevelopment());
			entity.setHoursLecture(vo.getHoursLecture());
			entity.setHoursPractical(vo.getHoursPractical());
			entity.setHoursExcursion(vo.getHoursExcursion());
			entity.setHoursSeminar(vo.getHoursSeminar());
			entity.setNote(vo.getNote());


		} catch (Exception e) {
			logger.error("toAssignment got a pesky exception: "+ e + e.getCause());
		} finally {
			return entity;
		}
	}

}