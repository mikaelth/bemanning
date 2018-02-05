package se.uu.ebc.bemanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Calendar;
import java.util.Comparator;


import se.uu.ebc.bemanning.entity.Staff;
import se.uu.ebc.bemanning.entity.Person;
import se.uu.ebc.bemanning.entity.Progress;
import se.uu.ebc.bemanning.entity.PhDPosition;
import se.uu.ebc.bemanning.repo.PhDPositionRepo;
import se.uu.ebc.bemanning.repo.ProgressRepo;
import se.uu.ebc.bemanning.repo.PersonRepo;

import se.uu.ebc.bemanning.vo.PhDPositionVO;
import se.uu.ebc.bemanning.vo.ProgressVO;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;


@Service
public class PhDService {

    private static Logger logger = Logger.getLogger(PhDService.class.getName());

	@Autowired
	PhDPositionRepo phdPositionRepo;

	@Autowired
	ProgressRepo progressRepo;
	
	@Autowired
	PersonRepo personRepo;

	@Autowired
	StaffingService staffService;

/* 
	public Set<Staff> getAllRelevantStaff(OrganisationUnit dept, String year) throws Exception {
		return staffRepo.getRelevantStaff(dept,year);
	}    

 	public List<Staff> getAssignedStaff (String year, OrganisationUnit dept) {
 	
		return staffRepo.findUserByOuListAndYear(dept.getExpandedOu(year),year);

 	}
 	
 */
 	
 	
 	public List<PhDPosition> allSorted () {
		List<PhDPosition> phds = new ArrayList<PhDPosition>(); 
		try {		
			phds.addAll(phdPositionRepo.findAll());
			logger.debug("MTh allSorted, phds "+ReflectionToStringBuilder.toString(phds, ToStringStyle.MULTI_LINE_STYLE));
			Collections.sort(phds, new CompPhDPositions());
			logger.debug("MTh allSorted, sortedphds "+ReflectionToStringBuilder.toString(phds, ToStringStyle.MULTI_LINE_STYLE));
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.debug("MTh allSorted, pesky exception "+ReflectionToStringBuilder.toString(phds, ToStringStyle.MULTI_LINE_STYLE));
				logger.error("MTh allSorted, pesky exception "+e);
			}
		} finally {
		
 			return phds;
 		}
 	}
 		
	private class CompPhDPositions implements Comparator<PhDPosition>{
 
		public int compare(PhDPosition e1, PhDPosition e2) {
		
			int reply = 0;
		
			Staff s1 = staffService.findUserByPersonAndYear(e1.getPerson(), String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
			String ou1 = s1 != null ? (s1.getOrganisationUnit() != null ? s1.getOrganisationUnit().getSvName() : "" ) : "";
			Staff s2 = staffService.findUserByPersonAndYear(e2.getPerson(), String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
			String ou2 = s2 != null ? (s2.getOrganisationUnit() != null ? s2.getOrganisationUnit().getSvName() : "" ) : "";
		
			if (ou1 == ou2) {
				reply = e1.getPerson().getFamilyName().compareTo(e2.getPerson().getFamilyName());
			} else {
				reply = ou1.compareTo(ou2);
			}
		

			return reply;
		}
	}


	/* PhD Positions */

	public List<PhDPositionVO> getAllPhDPositions() throws Exception {
		List<PhDPositionVO> pVO = new ArrayList<PhDPositionVO>();
		try {	
 			String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
			logger.debug("getAllPhDPositions year: "+year);

			for (PhDPosition p : phdPositionRepo.findAll()) {
				Staff s = staffService.findUserByPersonAndYear(p.getPerson(),year);
				logger.debug("getAllPhDPositions staff: "+s);
				String program = s == null ? "" : s.getOrganisationUnit().getSvName();			

 				pVO.add(new PhDPositionVO(p,program));
 			}
         	return pVO;        	        
        } catch (Exception e) {
			logger.error("getAllPhDPositions got a pesky exception: "+ e + e.getCause());

			return null;
			
        }
    }
    
    public PhDPositionVO savePhDPosition(PhDPositionVO pvo) throws Exception {
    	PhDPosition p = pvo.getId() == null ? toPhDPosition(pvo) : toPhDPosition(phdPositionRepo.findById(pvo.getId()), pvo);
    	phdPositionRepo.save(p);

		String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
		Staff s = staffService.findUserByPersonAndYear(p.getPerson(),year);
		return new PhDPositionVO(p, s == null ? "" : s.getOrganisationUnit().getSvName());
    
    }

    public synchronized void deletePhDPosition(Long pID) throws Exception {
		PhDPosition p = phdPositionRepo.findById(pID);
		phdPositionRepo.delete(p);
    }

   	
 
	private PhDPosition toPhDPosition (PhDPositionVO pvo) throws Exception {
		Progress prog = new Progress();
		prog.setDate(pvo.getStart());
		prog.setActivity(1.0f);
		prog.setProjectFraction(0.9f);
		prog.setGuFraction(0.1f);
		prog.setRemainingMonths(48.0f);
		
		PhDPosition phd = toPhDPosition (new PhDPosition(), pvo);
		phd.getProgresses().add(prog);
		prog.setPhdPosition(phd);
		
//		progressRepo.save(prog);

 		return phd;
   	}

	private PhDPosition toPhDPosition (PhDPosition p, PhDPositionVO pvo) throws Exception {


		try {

			p.setId(pvo.getId());
			p.setStart(pvo.getStart());
			p.setDissertation(pvo.getDissertation());
			p.setNote(pvo.getNote());
			p.setInactive(pvo.getInactive());

			p.setPerson(personRepo.findById(pvo.getPersonId()));
			
		} catch (Exception e) {
			logger.error("toPhDPosition got a pesky exception: "+ e + e.getCause());
		} finally {
			return p;
		}
	}
 
					
	
	/* Progresses */

	public List<ProgressVO> getAllProgress() throws Exception {
		List<ProgressVO> pVO = new ArrayList<ProgressVO>();
		try {	
			for (Progress p : progressRepo.findAll()) {
 				pVO.add(new ProgressVO(p));
 			}
         	return pVO;        	        
        } catch (Exception e) {
			logger.error("getAllProgress got a pesky exception: "+ e + e.getCause());

			return null;
			
        }
    }
    
    public ProgressVO saveProgress(ProgressVO pvo) throws Exception {
    	Progress p = pvo.getId() == null ? toProgress(pvo) : toProgress(progressRepo.findById(pvo.getId()), pvo);
    	progressRepo.save(p);
		return new ProgressVO(p);
    
    }

    public synchronized void deleteProgress(Long pID) throws Exception {
		Progress p = progressRepo.findById(pID);
		progressRepo.delete(p);
    }

   	
 
	private Progress toProgress (ProgressVO pvo) throws Exception {
 		return toProgress (new Progress(), pvo);
   	}

	private Progress toProgress (Progress p, ProgressVO pvo) throws Exception {


		try {

			p.setId(pvo.getId());

			p.setDate(pvo.getDate());
			p.setActivity(pvo.getActivity());
			p.setProjectFraction(pvo.getProjectFraction());
			p.setGuFraction(pvo.getGuFraction());
			p.setToEcoSys(pvo.getToEcoSys());
			p.setRemainingMonths(pvo.getRemainingMonths());
			p.setToUpDok(pvo.getToUpDok());
			p.setNote(pvo.getNote());
			p.setAddedMonths(pvo.getAddedMonths());

			p.setPhdPosition(phdPositionRepo.findById(pvo.getPhdPositionId()));
    
		} catch (Exception e) {
			logger.error("toProgress got a pesky exception: "+ e + e.getCause());
		} finally {
			return p;
		}
	}
	
	
}