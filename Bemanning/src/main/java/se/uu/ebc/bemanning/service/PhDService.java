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


import se.uu.ebc.bemanning.entity.OrganisationUnit;
import se.uu.ebc.bemanning.entity.Person;
import se.uu.ebc.bemanning.entity.Staff;
import se.uu.ebc.bemanning.entity.PhDPosition;
import se.uu.ebc.bemanning.entity.Assignment;
import se.uu.ebc.bemanning.security.UserRepo;
//import se.uu.ebc.bemanning.repo.StaffRepo;
import se.uu.ebc.bemanning.repo.PhDPositionRepo;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;


@Service
public class PhDService {

    private static Logger logger = Logger.getLogger(PhDService.class.getName());

/* 
	@Autowired
	UserRepo userRepo;

 */
	@Autowired
	PhDPositionRepo phdPositionRepo;

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

}