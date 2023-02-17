package se.uu.ebc.bemanning.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import se.uu.ebc.bemanning.service.CourseService;
import se.uu.ebc.bemanning.service.LuntanService;
import se.uu.ebc.bemanning.vo.CourseVO;
import se.uu.ebc.bemanning.vo.CourseInstanceVO;
import se.uu.ebc.bemanning.vo.CourseGrantVO;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;
import flexjson.transformer.NullTransformer;
import se.uu.ebc.bemanning.util.DateNullTransformer;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

@Controller
@RequestMapping(value = "/rest")
@CrossOrigin(origins = "http://localhost:1841", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class CourseController {

    private Logger logger = Logger.getLogger(CourseController.class.getName());

	@Autowired
	CourseService courseService;

	@Autowired
	LuntanService luntanService;

	/* Courses */

    @RequestMapping(value="/courses", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> allCourses() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        try {
 			return new ResponseEntity<String>(new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("courses").transform(new DateTransformer("yyyy-MM-dd"), "updated").deepSerialize(courseService.getAllCourses()), headers, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

	@PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    @RequestMapping(value="/courses/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateCourse(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
			CourseVO cVO = new JSONDeserializer<CourseVO>().use(null, CourseVO.class).deserialize(json);
			cVO.setId(id);
			cVO = courseService.saveCourse(cVO);

 			String restResponse = new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("courses").deepSerialize(cVO);
			restResponse = new StringBuilder(restResponse).insert(1, "success: true,").toString();

            return new ResponseEntity<String>(restResponse, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


	@PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    @RequestMapping(value="/courses", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createCourse(@RequestBody String json, UriComponentsBuilder uriBuilder) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
			CourseVO cVO = new JSONDeserializer<CourseVO>().use(null, CourseVO.class).use(Date.class, new DateTransformer("yyyy-MM-dd") ).deserialize(json);
			cVO = courseService.saveCourse(cVO);
            RequestMapping a = (RequestMapping) getClass().getAnnotation(RequestMapping.class);
            headers.add("Location",uriBuilder.path(a.value()[0]+"/"+cVO.getId().toString()).build().toUriString());

 			String restResponse = new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("courses").deepSerialize(cVO);
			restResponse = new StringBuilder(restResponse).insert(1, "success: true,").toString();

            return new ResponseEntity<String>(restResponse, headers, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


	@PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
	@RequestMapping(value = "/courses/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
	public ResponseEntity<String> deleteCourse(@PathVariable("id") Long id) {
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
			courseService.deleteCourse(id);
            return new ResponseEntity<String>("{success: true, id : " +id.toString() + "}", headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




	/* CourseInstances */

    @RequestMapping(value="/cis", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> allCourseInstances() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        try {
 			return new ResponseEntity<String>(new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("cis").transform(new DateNullTransformer("yyyy-MM-dd"), Date.class).serialize(courseService.getAllCourseInstances()), headers, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

	@PreAuthorize("hasRole('ROLE_DIRECTOROFSTUDIES')")
    @RequestMapping(value="/cis/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateCourseInstance(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
			CourseInstanceVO ciVO = new JSONDeserializer<CourseInstanceVO>().use(null, CourseInstanceVO.class).use(Date.class, new DateNullTransformer("yyyy-MM-dd") ).deserialize(json);
			ciVO.setId(id);
			ciVO = courseService.saveCourseInstance(ciVO);

 			String restResponse = new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("cis").transform(new DateNullTransformer("yyyy-MM-dd"), "startDate").transform(new DateNullTransformer("yyyy-MM-dd"), "endDate").serialize(ciVO);
			restResponse = new StringBuilder(restResponse).insert(1, "success: true,").toString();

            return new ResponseEntity<String>(restResponse, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


	@PreAuthorize("hasRole('ROLE_DIRECTOROFSTUDIES')")
    @RequestMapping(value="/cis", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createCourseInstance(@RequestBody String json, UriComponentsBuilder uriBuilder) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
			CourseInstanceVO ciVO = new JSONDeserializer<CourseInstanceVO>().use(null, CourseInstanceVO.class).use(Date.class, new DateNullTransformer("yyyy-MM-dd") ).deserialize(json);
			ciVO = courseService.saveCourseInstance(ciVO);
            RequestMapping a = (RequestMapping) getClass().getAnnotation(RequestMapping.class);
            headers.add("Location",uriBuilder.path(a.value()[0]+"/"+ciVO.getId().toString()).build().toUriString());

 			String restResponse = new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("cis").transform(new DateNullTransformer("yyyy-MM-dd"), "startDate").transform(new DateNullTransformer("yyyy-MM-dd"), "endDate").serialize(ciVO);
			restResponse = new StringBuilder(restResponse).insert(1, "success: true,").toString();

            return new ResponseEntity<String>(restResponse, headers, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


	@PreAuthorize("hasRole('ROLE_DIRECTOROFSTUDIES')")
	@RequestMapping(value = "/cis/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
	public ResponseEntity<String> deleteCourseInstance(@PathVariable("id") Long id) {
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
			courseService.deleteCourseInstance(id);
            return new ResponseEntity<String>("{success: true, id : " +id.toString() + "}", headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



	/* CourseGrants */

    @RequestMapping(value="/cgs", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> allCourseGrants() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        try {
// 			return new ResponseEntity<String>(new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("cgs").transform(new NullTransformer(), "setDate").transform(new DateTransformer("yyyy-MM-dd"), "setDate").serialize(courseService.getAllCourseGrants()), headers, HttpStatus.OK);
 			return new ResponseEntity<String>(new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("cgs").transform(new DateNullTransformer("yyyy-MM-dd"), "setDate").serialize(courseService.getAllCourseGrants()), headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("allCourseGrants got a pesky exception: "+ e + e.getCause());
			return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

	@PreAuthorize("hasRole('ROLE_DIRECTOROFSTUDIES')")
    @RequestMapping(value="/cgs/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateCourseGrant(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
			CourseGrantVO cgVO = new JSONDeserializer<CourseGrantVO>().use(null, CourseGrantVO.class).use(Date.class, new DateNullTransformer("yyyy-MM-dd") ).deserialize(json);
			logger.debug("updateCourseGrant, cgVO "+ReflectionToStringBuilder.toString(cgVO, ToStringStyle.MULTI_LINE_STYLE));
			cgVO.setId(id);
			cgVO = courseService.saveCourseGrant(cgVO);

 			String restResponse = new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("cgs").transform(new DateNullTransformer("yyyy-MM-dd"), "setDate").serialize(cgVO);
			restResponse = new StringBuilder(restResponse).insert(1, "success: true,").toString();

            return new ResponseEntity<String>(restResponse, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


	@PreAuthorize("hasRole('ROLE_DIRECTOROFSTUDIES')")
    @RequestMapping(value="/cgs", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createCourseGrant(@RequestBody String json, UriComponentsBuilder uriBuilder) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
			CourseGrantVO cgVO = new JSONDeserializer<CourseGrantVO>().use(null, CourseGrantVO.class).use(Date.class, new DateNullTransformer("yyyy-MM-dd") ).deserialize(json);
			cgVO = courseService.saveCourseGrant(cgVO);
            RequestMapping a = (RequestMapping) getClass().getAnnotation(RequestMapping.class);
            headers.add("Location",uriBuilder.path(a.value()[0]+"/"+cgVO.getId().toString()).build().toUriString());

 			String restResponse = new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("cgs").transform(new DateNullTransformer("yyyy-MM-dd"), "setDate").serialize(cgVO);
			restResponse = new StringBuilder(restResponse).insert(1, "success: true,").toString();

            return new ResponseEntity<String>(restResponse, headers, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


	@PreAuthorize("hasRole('ROLE_DIRECTOROFSTUDIES')")
	@RequestMapping(value = "/cgs/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
	public ResponseEntity<String> deleteCourseGrant(@PathVariable("id") Long id) {
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
			courseService.deleteCourseGrant(id);
            return new ResponseEntity<String>("{success: true, id : " +id.toString() + "}", headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value="/test-cgd", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> yearlyCGDs() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        try {
 			return new ResponseEntity<String>(new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("cgd").transform(new DateNullTransformer("yyyy-MM-dd"), "setDate").deepSerialize(luntanService.getLuntanCGD(2022)), headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("yearlyCGDs got a pesky exception: "+ e + e.getCause());
			return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
