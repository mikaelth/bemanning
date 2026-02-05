package se.uu.ebc.bemanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.dao.OptimisticLockingFailureException;

import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import se.uu.ebc.luntan.vo.CourseInstanceVO;

import se.uu.ebc.bemanning.entity.course.Course;
import se.uu.ebc.bemanning.vo.CourseVO;
import se.uu.ebc.bemanning.repo.CourseRepo;


import lombok.extern.slf4j.Slf4j;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;

@Slf4j
@Service
public class CourseService {

    @Autowired
    RestClient luntanCIRestClient;

    @Autowired
    CourseRepo courseRepo;

	private ModelMapper modelMapper = new ModelMapper();

	/* Courses */
	
	public List<CourseVO> getAllCourses() throws ResourceNotFoundException  {
		List<CourseVO> pVO = new ArrayList<CourseVO>();
			log.debug("getAllCourses()");
			for (Course p : courseRepo.findAll()) {
 				pVO.add(modelMapper.map(p, CourseVO.class));
  			}
         	return pVO;        	        
    }

	public CourseVO getById (Long id) {
		Course p = courseRepo.findById(id).get();
		return modelMapper.map(p, CourseVO.class);
	}   
		
	public CourseVO saveCourse(CourseVO pvo) throws Exception {
    	Course p = pvo.getId() == null ? toCourse(pvo) : toCourse(courseRepo.findById(pvo.getId()).get(), pvo);
    	courseRepo.save(p);
 		return modelMapper.map(p, CourseVO.class);
   
    }

	private Course toCourse (CourseVO pvo) throws Exception {
 		return toCourse (new Course(), pvo);
   	}

	private Course toCourse (Course p, CourseVO pvo) throws Exception {
		modelMapper.map(pvo, p);
		return p;
	}
    
	public synchronized void deleteCourse(Long pID) throws IllegalArgumentException, OptimisticLockingFailureException {
		courseRepo.deleteById(pID);
		return;
    }


	/* Course instances */
	
    public List<CourseInstanceVO> getCourseInstances() {
        Map<String,List<CourseInstanceVO>> courseInstances = luntanCIRestClient
        	.get()
        	.retrieve()
        	.body(new ParameterizedTypeReference<>() {});

       return courseInstances.get("cis");

    }

}
