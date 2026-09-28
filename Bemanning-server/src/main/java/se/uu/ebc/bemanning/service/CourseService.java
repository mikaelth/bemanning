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
import se.uu.ebc.bemanning.dto.CourseDTO;
import se.uu.ebc.bemanning.entity.course.Course;
import se.uu.ebc.bemanning.entity.courseinstance.CourseInstance;
import se.uu.ebc.bemanning.repo.CourseRepo;
import se.uu.ebc.bemanning.repo.CourseInstanceRepo;


import lombok.extern.slf4j.Slf4j;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;

@Slf4j
@Service
public class CourseService {


    private final RestClient luntanCIRestClient;
    private final CourseRepo courseRepo;
    private final CourseInstanceRepo ciRepo;


	public CourseService (RestClient luntanCI, CourseRepo courseRepo, CourseInstanceRepo ciRepo) {
		this.luntanCIRestClient=luntanCI;
		this.courseRepo = courseRepo;
		this.ciRepo = ciRepo;
	}
	/* Courses */
	
	public List<Course> getAllCourses() throws ResourceNotFoundException  {
		List<Course> courses = courseRepo.findAll();
		return courses;        	        
    }

	public Course getById (Long id) {
		return courseRepo.findById(id).orElseThrow();
	}   

	public Course saveCourse(Course course) throws Exception {	
		courseRepo.save(course);
		return course;
	}
	
	public synchronized void deleteCourse(Long pID) throws IllegalArgumentException, OptimisticLockingFailureException {
		courseRepo.deleteById(pID);
		return;
    }

	public synchronized void deleteCourse(Course c) throws IllegalArgumentException, OptimisticLockingFailureException {
		courseRepo.delete(c);
		return;
    }
	
/* 
	public CourseDTO saveCourse(CourseDTO pvo) throws Exception {
    	Course p = pvo.getId() == null ? toCourse(pvo) : toCourse(courseRepo.findById(pvo.getId()).get(), pvo);
    	courseRepo.save(p);
 		return modelMapper.map(p, CourseDTO.class);
   
    }

	private Course toCourse (CourseDTO pvo) throws Exception {
 		return toCourse (new Course(), pvo);
   	}

	private Course toCourse (Course p, CourseDTO pvo) throws Exception {
		modelMapper.map(pvo, p);
		return p;
	}
    
	public synchronized void deleteCourse(Long pID) throws IllegalArgumentException, OptimisticLockingFailureException {
		courseRepo.deleteById(pID);
		return;
    }

 */

	/* Course instances */
	
	public List<CourseInstance> getAllCourseInstances() throws ResourceNotFoundException  {
		List<CourseInstance> courses = ciRepo.findAll();
		return courses;        	        
    }

	public CourseInstance getCourseInstanceById (Long id) {
		return ciRepo.findById(id).orElseThrow();
	}   

	public List<CourseInstance> getCourseInstancesByYear (String year) {
		return ciRepo.findByYear(year);
	}   

	public CourseInstance saveCourseInstance(CourseInstance course) throws Exception {	
		ciRepo.save(course);
		return course;
	}
	
	public synchronized void deleteCourseInstance(Long pID) throws IllegalArgumentException, OptimisticLockingFailureException {
		ciRepo.deleteById(pID);
		return;
    }

	public synchronized void deleteCourseInstance(CourseInstance ci) throws IllegalArgumentException, OptimisticLockingFailureException {
		ciRepo.delete(ci);
		return;
    }
	
	
    public List<CourseInstanceVO> getCourseInstances() {
        Map<String,List<CourseInstanceVO>> courseInstances = luntanCIRestClient
        	.get()
        	.retrieve()
        	.body(new ParameterizedTypeReference<>() {});

       return courseInstances.get("cis");

    }

}
