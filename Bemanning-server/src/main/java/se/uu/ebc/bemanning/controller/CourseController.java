package se.uu.ebc.bemanning.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import se.uu.ebc.bemanning.service.CourseService;
import se.uu.ebc.luntan.vo.CourseInstanceVO;

import java.util.List;
import java.util.Map;


@RestController
public class CourseController {

	@Autowired
	CourseService courseService;


    @GetMapping(value = "/coursesinstances/")
    public List<CourseInstanceVO> courseInstances() {
    	return courseService.getCourseInstances();
    }
}
