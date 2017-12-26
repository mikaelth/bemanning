package se.uu.ebc.bemanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import se.uu.ebc.bemanning.vo.CourseVO;
import se.uu.ebc.bemanning.entity.Course;
import se.uu.ebc.bemanning.enums.UserRoleType;
import se.uu.ebc.bemanning.repo.CourseRepo;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;

@Service
public class CourseService {

    private static Logger logger = Logger.getLogger(CourseService.class.getName());

	@Autowired
	CourseRepo courseRepo;


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
 
    
}