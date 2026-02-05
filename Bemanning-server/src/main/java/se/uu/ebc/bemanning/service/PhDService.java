package se.uu.ebc.bemanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Calendar;
import java.util.Comparator;

import java.time.Year;

import jakarta.annotation.PostConstruct;
import se.uu.ebc.bemanning.entity.Progress;
import se.uu.ebc.bemanning.entity.staff.Staff;
import se.uu.ebc.bemanning.entity.PhDPosition;
import se.uu.ebc.bemanning.repo.PhDPositionRepo;
import se.uu.ebc.bemanning.repo.ProgressRepo;
import se.uu.ebc.bemanning.repo.PersonRepo;

import se.uu.ebc.bemanning.vo.PhDPositionVO;
import se.uu.ebc.bemanning.vo.ProgressVO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.dao.OptimisticLockingFailureException;


@Slf4j
@Service
public class PhDService {


	@Autowired
	PhDPositionRepo phdPositionRepo;

	@Autowired
	ProgressRepo progressRepo;
	
	@Autowired
	PersonRepo personRepo;

	@Autowired
	StaffService staffService;
	
	private ModelMapper mapper = new ModelMapper();
	private ModelMapper progressModelMapper = new ModelMapper();
	
	private TypeMap<PhDPosition, PhDPositionVO> phdToVOMapper = mapper.createTypeMap(PhDPosition.class, PhDPositionVO.class);
	private TypeMap<PhDPositionVO,PhDPosition> voTophdMapper = mapper.createTypeMap(PhDPositionVO.class, PhDPosition.class);

/* 
	public Set<Staff> getAllRelevantStaff(OrganisationUnit dept, String year) throws Exception {
		return staffRepo.getRelevantStaff(dept,year);
	}    

 	public List<Staff> getAssignedStaff (String year, OrganisationUnit dept) {
 	
		return staffRepo.findUserByOuListAndYear(dept.getExpandedOu(year),year);

 	}
 	
 */
 	
 
	@PostConstruct
	public void init () {
 		phdToVOMapper.addMapping(PhDPosition::currentRemainingProjectTime, PhDPositionVO::setCurrentRemainingProjectTime);
 		phdToVOMapper.addMapping(PhDPosition::predictedFinishDate, PhDPositionVO::setPredictedFinishDate);
 		phdToVOMapper.addMapping(PhDPosition::predictedHalfTime, PhDPositionVO::setPredictedHalfTime);
 		phdToVOMapper.addMapping(PhDPosition::predicted80Percent, PhDPositionVO::setPredicted80Percent);
	}
 	
 	
 	
 	
 	public List<PhDPosition> allSorted () {
		List<PhDPosition> phds = new ArrayList<PhDPosition>(); 
		try {		
			phds.addAll(phdPositionRepo.findAll());
			Collections.sort(phds, new CompPhDPositions());
		} catch (Exception e) {
				log.error("MTh allSorted, pesky exception "+e);
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

	public List<PhDPositionVO> getAllPhDPositions() throws ResourceNotFoundException {
		List<PhDPositionVO> pVOs = new ArrayList<PhDPositionVO>();


 			String year = String.valueOf(Year.now().getValue());
 			
			log.debug("getAllPhDPositions year: "+year);

			for (PhDPosition p : phdPositionRepo.findAll()) {
				log.debug("getAllPhDPositions person: "+p.getPerson().getName());
				Staff s = staffService.findUserByPersonAndYear(p.getPerson(),year);
				log.debug("getAllPhDPositions staff: "+s);
				String program = s == null ? "" : s.getOrganisationUnit().getSvName();			

 				PhDPositionVO pVO = mapper.map(p,PhDPositionVO.class);
 				pVO.setProgram(program);
 				pVOs.add(pVO);
 			}
         	return pVOs;        	        

    }

	public PhDPositionVO getPhDById (Long id) {
		log.debug("getById()");
		PhDPosition p = phdPositionRepo.findById(id).get();
		log.debug(p.toString());
		return mapper.map(p, PhDPositionVO.class);
	}   
    
    public PhDPositionVO savePhDPosition(PhDPositionVO pvo) throws Exception {
    	PhDPosition p = pvo.getId() == null ? toPhDPosition(pvo) : toPhDPosition(phdPositionRepo.findById(pvo.getId()).get(), pvo);
    	phdPositionRepo.save(p);

		String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
		Staff s = staffService.findUserByPersonAndYear(p.getPerson(),year);
		PhDPositionVO pVO = mapper.map(p,PhDPositionVO.class);
 		pVO.setProgram(s == null ? "" : s.getOrganisationUnit().getSvName());

		return pVO;
    
    }

    public synchronized void deletePhDPosition(Long pID) throws IllegalArgumentException, OptimisticLockingFailureException {
		phdPositionRepo.deleteById(pID);
    }
   	 
	private PhDPosition toPhDPosition (PhDPositionVO pvo) throws Exception {
		return toPhDPosition (new PhDPosition(),pvo);
   	}

	private PhDPosition toPhDPosition (PhDPosition p, PhDPositionVO pvo) throws Exception {
		mapper.map(pvo,p);
		return p;
	}
 
					
	
	/* Progresses */

	public List<ProgressVO> getAllProgress() throws ResourceNotFoundException  {
		List<ProgressVO> pVO = new ArrayList<ProgressVO>();
		for (Progress p : progressRepo.findAll()) {
 			pVO.add(progressModelMapper.map(p, ProgressVO.class));
 		}
        return pVO;        	        

			
    }
   
    
    public ProgressVO saveProgress(ProgressVO pvo) throws Exception {
    	Progress p = pvo.getId() == null ? toProgress(pvo) : toProgress(progressRepo.findById(pvo.getId()).get(), pvo);
    	progressRepo.save(p);
		return progressModelMapper.map(p,ProgressVO.class);
    
    }

    public synchronized void deleteProgress(Long pID) throws IllegalArgumentException, OptimisticLockingFailureException {
		progressRepo.deleteById(pID);
    }	
 
	private Progress toProgress (ProgressVO pvo) throws Exception {
 		return toProgress (new Progress(), pvo);
   	}

	private Progress toProgress (Progress p, ProgressVO pvo) throws Exception {
		progressModelMapper.map(pvo,p);
		return p;

	}
	
	
}