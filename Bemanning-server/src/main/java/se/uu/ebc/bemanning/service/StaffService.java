package se.uu.ebc.bemanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.time.Instant;
import java.time.Duration;

/* 
import se.uu.ebc.bemanning.vo.PersonVO;
import se.uu.ebc.bemanning.entity.Person;
import se.uu.ebc.bemanning.repo.PersonRepo;
 */

import se.uu.ebc.bemanning.vo.StaffVO;

import se.uu.ebc.bemanning.entity.Person;
import se.uu.ebc.bemanning.entity.Staff;
import se.uu.ebc.bemanning.entity.MaxCost;
import se.uu.ebc.bemanning.entity.OrganisationUnit;

import se.uu.ebc.bemanning.enums.UserRoleType;
import se.uu.ebc.bemanning.enums.EmploymentType;

import se.uu.ebc.bemanning.repo.StaffRepo;
import se.uu.ebc.bemanning.repo.OrganisationUnitRepo;
import se.uu.ebc.bemanning.repo.MaxCostRepo;

import org.modelmapper.ModelMapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;

@Slf4j
@Service
public class StaffService {


/* 
	@Autowired
	private PersonRepo personRepo;

 */
	private ModelMapper modelMapper = new ModelMapper();
 
	@Autowired
	private StaffRepo staffRepo;

	@Autowired
	private OrganisationUnitRepo ouRepo;

/*	@Autowired
	private MaxCostRepo mcRepo;

	@Autowired
	private AKKAService akka;
 */
	
/* 
	@Autowired
	private AKKAUserRepo ldapRepo;
 */

	/* Staff */

	public List<StaffVO> getAllStaff() throws ResourceNotFoundException  {
		List<StaffVO> sVO = new ArrayList<StaffVO>();
			log.debug("getAllStaff()");
			for (Staff s : staffRepo.findAll()) {
 				sVO.add(modelMapper.map(s, StaffVO.class));
 				
 			}
         	return sVO;        	        
	
    }

	public StaffVO getById (Long id) {
		log.debug("getById()");
		Staff s = staffRepo.findById(id).get();
		log.debug(s.toString());
		return modelMapper.map(s, StaffVO.class);
//		return new StaffVO(s);
	}   
	
	public StaffVO saveStaff(StaffVO svo) throws Exception {
    	Staff s = svo.getId() == null ? toStaff(svo) : toStaff(staffRepo.findById(svo.getId()).get(), svo);
    	staffRepo.save(s);
 		return modelMapper.map(s, StaffVO.class);
   
    }

	private Staff toStaff (StaffVO svo) throws Exception {
 		return toStaff (new Staff(), svo);
   	}

	private Staff toStaff (Staff s, StaffVO svo) throws Exception {
		modelMapper.map(svo, s);
		return s;
	}
    
	public synchronized void deleteStaff(Long sID) throws IllegalArgumentException, OptimisticLockingFailureException {
		staffRepo.deleteById(sID);
		return;
    }

   	
  	public Staff findUserByPersonAndYear(Person person, String year) {
 		
 		List<Staff> staff = staffRepo.findUserByPersonAndYear(person,year);
 		return staff.size() > 0 ? staff.get(0) : null;
 	}
				
	/* Staff */

/* 

	public List<StaffVO> getAllStaff() throws Exception {
		List<StaffVO> svo = new ArrayList<StaffVO>();
		try {	
logger.debug("getAllStaff, begin findAll");
Instant start = Instant.now();
staffRepo.findAll();
Instant end = Instant.now();
logger.debug("getAllStaff, done findAll, took " + Duration.between(start,end));

			for (Staff s : staffRepo.findAll()) {
 				svo.add(new StaffVO(s));
// 				svo.add(new StaffVO(s,0.0f));
 			}
end = Instant.now();
logger.debug("getAllStaff, done findAll, took " + Duration.between(start,end));
         	return svo;        	        
        } catch (Exception e) {

			logger.debug("getAllStaff caught a pesky exception, " + e);
			return null;
			
        }
    }
    
    public StaffVO saveStaff(StaffVO svo) throws Exception {
    	Staff s = svo.getId() == null ? toStaff(svo) : toStaff(staffRepo.findById(svo.getId()), svo);
    	staffRepo.save(s);
//		return new StaffVO(s,getPreviousUb(s));
		logger.error("saveStaff saved staff: "+ s);
		return new StaffVO(s);
    
    }

    public synchronized void deleteStaff(Long id) throws Exception {
		Staff s = staffRepo.findById(id);
		staffRepo.delete(s);
    }


	private Staff toStaff (StaffVO svo) throws Exception {
 		return toStaff (new Staff(), svo);
   	}

	private Staff toStaff (Staff s, StaffVO vo) throws Exception {
		try {
			s.setId(vo.getId());

			s.setPerson(personRepo.findById(vo.getPersonId()));
			s.setOrganisationUnit(ouRepo.findById(vo.getOrganisationUnitId()));

			s.setPercentGU(vo.getPercentGU());

			s.setPosition(vo.getPosition());
			s.setHourlyCharge(vo.getHourlyCharge());
			s.setYear(vo.getYear());
			s.setNote(vo.getNote());
			s.setIb(vo.getIb());
	
			MaxCost m = mcRepo.findByCategoryYear(s.getPosition(), s.getYear());
			if (m == null){
				m = new MaxCost(s.getPosition(), s.getYear());
				mcRepo.save(m);
			}
			s.setMaxCost(m);

		} catch (Exception e) {
			logger.error("toStaff got a pesky exception: "+ e + e.getCause());
		} finally {
			return s;
		}
	}
 */

/* 
    private Float getPreviousUb(Staff s) {
    	Float ub = 0.0f;
		try {
    		Staff os = staffRepo.findUserByPersonYearAndPosition(s.getPerson(), Integer.toString(Integer.parseInt( s.getYear())-1), s.getPosition());

			logger.debug("getPreviousUb; s " + s + "; os " + os);

    		if (os != null) {
    			ub = os.getUb();  
    		} 
    		
		} catch (Exception e) {
			logger.error("getPreviousUb got a pesky exception " + e);
		}	
		finally {
			return ub;
		}
    }
 */
    
    
	public List<Staff> getAssignedStaff (String year, OrganisationUnit dept) 
	{
		return staffRepo.findUserByOuListAndYear(dept.getExpandedOu(year),year);
 	}

}