package se.uu.ebc.bemanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import se.uu.ebc.bemanning.vo.PersonVO;
import se.uu.ebc.bemanning.entity.Person;
import se.uu.ebc.bemanning.enums.UserRoleType;
import se.uu.ebc.bemanning.repo.PersonRepo;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;

@Service
public class PeopleService {

    private static Logger logger = Logger.getLogger(PeopleService.class.getName());

	@Autowired
	PersonRepo personRepo;


	public List<PersonVO> getAllPersons() throws Exception {
		List<PersonVO> pVO = new ArrayList<PersonVO>();
		try {	
			for (Person p : personRepo.findAll()) {
 				pVO.add(new PersonVO(p));
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
 
    
}