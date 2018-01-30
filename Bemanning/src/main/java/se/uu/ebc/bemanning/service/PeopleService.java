package se.uu.ebc.bemanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.time.Instant;
import java.time.Duration;

import se.uu.ebc.bemanning.vo.PersonVO;
import se.uu.ebc.bemanning.vo.StaffVO;
import se.uu.ebc.bemanning.entity.Person;
import se.uu.ebc.bemanning.entity.Staff;
import se.uu.ebc.bemanning.entity.MaxCost;
import se.uu.ebc.bemanning.enums.UserRoleType;
import se.uu.ebc.bemanning.enums.EmploymentType;
import se.uu.ebc.bemanning.repo.PersonRepo;
import se.uu.ebc.bemanning.repo.StaffRepo;
import se.uu.ebc.bemanning.repo.OrganisationUnitRepo;
import se.uu.ebc.bemanning.repo.MaxCostRepo;
import se.uu.ebc.ldap.AKKAUserRepo;
import se.uu.ebc.ldap.AKKAUser;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;

@Service
public class PeopleService {

    private static Logger logger = Logger.getLogger(PeopleService.class.getName());

	@Autowired
	private PersonRepo personRepo;

	@Autowired
	private StaffRepo staffRepo;

	@Autowired
	private OrganisationUnitRepo ouRepo;

	@Autowired
	private MaxCostRepo mcRepo;

	@Autowired
	private AKKAService akka;
	
/* 
	@Autowired
	private AKKAUserRepo ldapRepo;
 */

	/* Person */

	public List<PersonVO> getAllPersons() throws Exception {
		List<PersonVO> pVO = new ArrayList<PersonVO>();
		try {	
			logger.debug("getAllPersons()");
			for (Person p : personRepo.findAll()) {
 				pVO.add(new PersonVO(p));
// Map<String,String> ldapSessionMap = akka.doLookup(p);
// logger.debug("mail: "+(ldapSessionMap.containsKey("mail") ? ldapSessionMap.get("mail") :"") +", phone: " + (ldapSessionMap.containsKey("telephoneNumber") ? ldapSessionMap.get("telephoneNumber") :""));

//				logger.debug(ReflectionToStringBuilder.toString(akka.doLookup(p), ToStringStyle.MULTI_LINE_STYLE));
 			}
         	return pVO;        	        
        } catch (Exception e) {

			return null;
			
        }
    }
    
    public PersonVO savePerson(PersonVO pvo) throws Exception {
    	Person p = pvo.getId() == null ? toPerson(pvo) : toPerson(personRepo.findById(pvo.getId()), pvo);
    	personRepo.save(p);
		return new PersonVO(p);
    
    }

    public synchronized void deletePerson(Long pID) throws Exception {
		Person p = personRepo.findById(pID);
		personRepo.delete(p);
    }

   	
 
	private Person toPerson (PersonVO pvo) throws Exception {
 		return toPerson (new Person(), pvo);
   	}

	private Person toPerson (Person p, PersonVO pvo) throws Exception {


		try {
			p.setId(pvo.getId());
			p.setGivenName(pvo.getGivenName());
			p.setFamilyName(pvo.getFamilyName());
			p.setNote(pvo.getNote());
			p.setUsername(pvo.getUsername());
			p.setIsActive(pvo.getIsActive());
			p.setFamilyFirst(pvo.getFamilyFirst());
		
			p.setUserRoles(pvo.getUserRoles());		


		} catch (Exception e) {
			logger.error("toPerson got a pesky exception: "+ e + e.getCause());
		} finally {
			return p;
		}
	}
 
				
	/* Staff */


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
    
}