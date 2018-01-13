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
 
import se.uu.ebc.bemanning.service.AssignmentService;
import se.uu.ebc.bemanning.vo.AssignmentVO;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

@Controller
@RequestMapping(value = "/rest")
@CrossOrigin(origins = "http://localhost:1841", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class AssignmentController {

    private Logger logger = Logger.getLogger(PersonController.class.getName());

	@Autowired
	AssignmentService asnmtService;

	/* Assignments */
		
    @RequestMapping(value="/assignments", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> allAssignments() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        try {
 			return new ResponseEntity<String>(new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("assignments").transform(new DateTransformer("yyyy-MM-dd"), "updated").serialize(asnmtService.getAllAssignments()), headers, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
  	
    }
 
	@PreAuthorize("hasRole('ROLE_DIRECTOROFSTUDIES')")
    @RequestMapping(value="/assignments/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateAssignment(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
			AssignmentVO asnmtVO = new JSONDeserializer<AssignmentVO>().use(null, AssignmentVO.class).deserialize(json);
			asnmtVO.setId(id);
			asnmtVO = asnmtService.saveAssignment(asnmtVO);
			
 			String restResponse = new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("assignments").deepSerialize(asnmtVO);
			restResponse = new StringBuilder(restResponse).insert(1, "success: true,").toString();

            return new ResponseEntity<String>(restResponse, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

 
	@PreAuthorize("hasRole('ROLE_DIRECTOROFSTUDIES')")
    @RequestMapping(value="/assignments", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createAssignment(@RequestBody String json, UriComponentsBuilder uriBuilder) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
			AssignmentVO asnmtVO = new JSONDeserializer<AssignmentVO>().use(null, AssignmentVO.class).use(Date.class, new DateTransformer("yyyy-MM-dd") ).deserialize(json);
			asnmtVO = asnmtService.saveAssignment(asnmtVO);
            RequestMapping a = (RequestMapping) getClass().getAnnotation(RequestMapping.class);
            headers.add("Location",uriBuilder.path(a.value()[0]+"/"+asnmtVO.getId().toString()).build().toUriString());

 			String restResponse = new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("assignments").serialize(asnmtVO);
			restResponse = new StringBuilder(restResponse).insert(1, "success: true,").toString();

            return new ResponseEntity<String>(restResponse, headers, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


	@PreAuthorize("hasRole('ROLE_DIRECTOROFSTUDIES')")
	@RequestMapping(value = "/assignments/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
	public ResponseEntity<String> deleteAssignment(@PathVariable("id") Long id) {
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
			asnmtService.deleteAssignment(id);
            return new ResponseEntity<String>("{success: true, id : " +id.toString() + "}", headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


	
	
} 
