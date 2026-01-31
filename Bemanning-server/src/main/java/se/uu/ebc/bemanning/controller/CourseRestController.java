package se.uu.ebc.bemanning.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import lombok.extern.slf4j.Slf4j;

import se.uu.ebc.bemanning.service.CourseService;
import se.uu.ebc.luntan.vo.CourseInstanceVO;
import se.uu.ebc.bemanning.vo.CourseVO;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;


@RestController
@RequestMapping(value = "/rest")
@CrossOrigin(origins = "http://localhost:1841")
@Slf4j
public class CourseRestController {

	@Autowired
	CourseService courseService;

	private record DeleteStatus (Boolean sucess, Long id) {}
	private record CreateCourseStatus (Boolean sucess, CourseVO courses) {}
	private record Courses (List<CourseVO> courses) {};


	/* Courses */	

    @GetMapping(value="/courses")
    public ResponseEntity<Courses> getAllEntities() {
		return ResponseEntity.ok(new Courses (courseService.getAllCourses() ));
    }

    @GetMapping(value="/courses/{id}")
    public CourseVO getEntity(@PathVariable Long id) {
		return courseService.getById(id);
    }

	@PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    @PostMapping(value="/courses")
	public ResponseEntity<CreateCourseStatus> createEntity(@Valid @RequestBody CourseVO pVO) throws Exception {
		CourseVO npVO = courseService.saveCourse(pVO);
		return ResponseEntity.ok(new CreateCourseStatus(true,npVO));
	}

	@PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    @PutMapping(value="/courses/{id}")
    public ResponseEntity<CreateCourseStatus> updateEntity(@Valid @RequestBody CourseVO pVO, @PathVariable Long id) throws Exception {
		if (pVO.getId().equals(id)) {
			CourseVO npVO = courseService.saveCourse(pVO);
			return ResponseEntity.ok(new CreateCourseStatus(true,npVO));
		} else {
			throw ( new IllegalArgumentException() );
		}
    }


	@PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
	@DeleteMapping(value = "/courses/{id}")
	public ResponseEntity<DeleteStatus> deleteEntity(@PathVariable Long id) {
		courseService.deleteCourse(id);
		return ResponseEntity.ok(new DeleteStatus(true,id));
    }

}
