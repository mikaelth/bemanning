package se.uu.ebc.bemanning.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import se.uu.ebc.bemanning.service.CourseService;
import se.uu.ebc.luntan.vo.CourseInstanceVO;

import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin(origins = "http://localhost:1841")
public class CourseInstanceController {

	@Autowired
	CourseService courseService;


    @GetMapping(value = "/coursesinstances/")
    public List<CourseInstanceVO> courseInstances() {
    	return courseService.getCourseInstances();
    }
}
