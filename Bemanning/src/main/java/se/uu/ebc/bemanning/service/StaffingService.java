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
import se.uu.ebc.bemanning.repo.StaffRepo;
import se.uu.ebc.bemanning.repo.CourseInstanceRepo;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.apache.log4j.Logger;


@Service
public class StaffingService {

    private static Logger logger = Logger.getLogger(StaffingService.class.getName());

/* 
	@Autowired
	UserRepo userRepo;

 */
	@Autowired
	StaffRepo staffRepo;

	@Autowired
	CourseInstanceRepo ciRepo;

	public List<CourseInstance> getRelevantCourseAssignments(String dept, String year, String[] ous) throws Exception {

		logger.debug("getRelevantCourseAssignments, dept " + dept + ", year " + year );
		logger.debug("getRelevantCourseAssignments, ous "+ReflectionToStringBuilder.toString(ous, ToStringStyle.MULTI_LINE_STYLE));			

//		return new ArrayList<CourseInstance>();
		return ciRepo.loadInvolvedCourses(ous, dept, year);
	}    
 
    public List<CourseInstance> getCourseAssignments(String year)
        throws java.lang.Exception
    {
		return ciRepo.findByYear(year);
	}


    public List<CourseInstance> getCourseAssignments(String year, Staff user)
        throws java.lang.Exception
    {		
		return ciRepo.findByYearAndCourseLeader(year, user);
	}

 	public List<Staff> getAssignedStaff (String year, OrganisationUnit dept) {
 	
		return staffRepo.findUserByOuListAndYear(dept.getExpandedOu(year),year);

 	}
 	
 	public Staff findUserByPersonAndYear(Person person, String year) {
 		
 		List<Staff> staff = staffRepo.findUserByPersonAndYear(person,year);
 		return staff.size() > 0 ? staff.get(0) : null;
 	}

}