package se.uu.ebc.bemanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import se.uu.ebc.bemanning.entity.Course;
import se.uu.ebc.bemanning.entity.CourseGrant;
import se.uu.ebc.bemanning.entity.CourseInstance;
import se.uu.ebc.bemanning.entity.Staff;

import se.uu.ebc.bemanning.enums.UserRoleType;

import se.uu.ebc.bemanning.repo.CourseGrantRepo;
import se.uu.ebc.bemanning.repo.CourseInstanceRepo;
import se.uu.ebc.bemanning.repo.CourseRepo;
import se.uu.ebc.bemanning.repo.OrganisationUnitRepo;
import se.uu.ebc.bemanning.repo.StaffRepo;

import se.uu.ebc.bemanning.vo.CourseGrantVO;
import se.uu.ebc.bemanning.vo.CourseInstanceVO;
import se.uu.ebc.bemanning.vo.CourseVO;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;

@Service
public class CourseService {

    private static Logger logger = Logger.getLogger(CourseService.class.getName());

	@Autowired
	CourseRepo courseRepo;

	@Autowired
	CourseInstanceRepo ciRepo;

	@Autowired
	CourseGrantRepo cgRepo;

	@Autowired
	StaffRepo staffRepo;

	@Autowired
	OrganisationUnitRepo ouRepo;


	/* Courses */
	
	public List<CourseVO> getAllCourses() throws Exception {
		List<CourseVO> cVO = new ArrayList<CourseVO>();
		try {	
			for (Course c : courseRepo.findAll()) {
 				cVO.add(new CourseVO(c));
 			}
         	return cVO;        	        
        } catch (Exception e) {

			return null;
			
        }
    }
    

    public CourseVO saveCourse(CourseVO cVO) throws Exception {
    	Course c = cVO.getId() == null ? toCourse(cVO) : toCourse(courseRepo.findById(cVO.getId()), cVO);
    	courseRepo.save(c);
		return new CourseVO(c);
    
    }


    public synchronized void deleteCourse(Long cID) throws Exception {
		Course c = courseRepo.findById(cID);
		courseRepo.delete(c);
    }

 
	private Course toCourse (CourseVO cvo) throws Exception {
 		return toCourse (new Course(), cvo);
   	}

	private Course toCourse (Course c, CourseVO cvo) throws Exception {


		try {
			c.setId(cvo.getId()) ;
			c.setCode(cvo.getCode()) ;
			c.setSeName(cvo.getSeName()) ;
			c.setEnName(cvo.getEnName()) ;
			c.setCourseGroup(cvo.getCourseGroup()) ;
			c.setPeriod(cvo.getPeriod()) ;
			c.setNote(cvo.getNote()) ;
			c.setCredits(cvo.getCredits()) ;


		} catch (Exception e) {
			logger.error("toCourse got a pesky exception: "+ e + e.getCause());
		} finally {
			return c;
		}
	}
 



	/* CourseInstances */
	
	public List<CourseInstanceVO> getAllCourseInstances() throws Exception {
		List<CourseInstanceVO> cVO = new ArrayList<CourseInstanceVO>();
		try {	
			for (CourseInstance ci : ciRepo.findAll()) {
 				cVO.add(new CourseInstanceVO(ci));
 			}
         	return cVO;        	        
        } catch (Exception e) {

			return null;
			
        }
    }
    

    public CourseInstanceVO saveCourseInstance(CourseInstanceVO cVO) throws Exception {
    	CourseInstance ci = cVO.getId() == null ? toCourseInstance(cVO) : toCourseInstance(ciRepo.findById(cVO.getId()), cVO);
    	ciRepo.save(ci);
		return new CourseInstanceVO(ci);
    
    }


    public synchronized void deleteCourseInstance(Long cID) throws Exception {
		CourseInstance ci = ciRepo.findById(cID);
		ciRepo.delete(ci);
    }

 
	private CourseInstance toCourseInstance (CourseInstanceVO cVO) throws Exception {
 		return toCourseInstance (new CourseInstance(), cVO);
   	}

	private CourseInstance toCourseInstance (CourseInstance ci, CourseInstanceVO cVO) throws Exception {


		try {
			ci.setId(cVO.getId());
			ci.setYear(cVO.getYear());
			ci.setExtraDesignation(cVO.getExtraDesignation());
			ci.setStartDate(cVO.getStartDate());
			ci.setEndDate(cVO.getEndDate());
			ci.setNote(cVO.getNote());
			ci.setNumberOfStudents(cVO.getNumberOfStudents());

			ci.setCourse(courseRepo.findById(cVO.getCourseId()));
			ci.setCourseLeader(staffRepo.findById(cVO.getCourseLeaderId()));




		} catch (Exception e) {
			logger.error("toCourseInstance got a pesky exception: "+ e + e.getCause());
		} finally {
			return ci;
		}
	}



	/* CourseGrants */
	
	public List<CourseGrantVO> getAllCourseGrants() throws Exception {
		List<CourseGrantVO> cgVO = new ArrayList<CourseGrantVO>();
		try {	
			for (CourseGrant cg : cgRepo.findAll()) {
 				cgVO.add(new CourseGrantVO(cg));
 			}
         	return cgVO;        	        
        } catch (Exception e) {

			return null;
			
        }
    }
    

    public CourseGrantVO saveCourseGrant(CourseGrantVO cgVO) throws Exception {
    	CourseGrant cg = cgVO.getId() == null ? toCourseGrant(cgVO) : toCourseGrant(cgRepo.findById(cgVO.getId()), cgVO);
    	cgRepo.save(cg);
		return new CourseGrantVO(cg);
    
    }


    public synchronized void deleteCourseGrant(Long cID) throws Exception {
		CourseGrant cg = cgRepo.findById(cID);
		cgRepo.delete(cg);
    }

 
	private CourseGrant toCourseGrant (CourseGrantVO cgVO) throws Exception {
 		return toCourseGrant (new CourseGrant(), cgVO);
   	}

	private CourseGrant toCourseGrant (CourseGrant entity, CourseGrantVO vo) throws Exception {


		try {

			entity.setId(vo.getId());
			entity.setAmount(vo.getAmount());
			entity.setType(vo.getType());
			entity.setNote(vo.getNote());
			entity.setSetDate(vo.getSetDate());

			entity.setCourseInstance(ciRepo.findById(vo.getCourseInstanceId()));
			entity.setDebitUnit(ouRepo.findById(vo.getDebitUnitId()));
			entity.setDepartment(ouRepo.findById(vo.getDepartmentId()));


		} catch (Exception e) {
			logger.error("toCourseGrant got a pesky exception: "+ e + e.getCause());
		} finally {
			return entity;
		}
	}
    
}