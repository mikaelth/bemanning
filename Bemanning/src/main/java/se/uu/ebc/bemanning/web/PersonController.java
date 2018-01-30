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
 
import se.uu.ebc.bemanning.security.UserRepo;
import se.uu.ebc.bemanning.repo.PersonRepo;
import se.uu.ebc.bemanning.entity.Person;
import se.uu.ebc.bemanning.service.PeopleService;
import se.uu.ebc.bemanning.vo.PersonVO;
import se.uu.ebc.bemanning.vo.StaffVO;
import se.uu.ebc.bemanning.vo.UserVO;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

@Controller
@RequestMapping(value = "/rest")
@CrossOrigin(origins = "http://localhost:1841", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class PersonController {

    private Logger logger = Logger.getLogger(PersonController.class.getName());

	@Autowired
	PersonRepo personRepo;

	@Autowired
	PeopleService peopleService;

	/* Persons */
		
    @RequestMapping(value="/people", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> allPeople() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        try {
// 			return new ResponseEntity<String>(new JSONSerializer().prettyPrint(true).exclude("*.class","*.phDPosition","*.staff","*.userRoles").rootName("people").transform(new DateTransformer("yyyy-MM-dd"), "updated").deepSerialize(personRepo.findAll()), headers, HttpStatus.OK);
 			return new ResponseEntity<String>(new JSONSerializer().prettyPrint(true).exclude("*.class","*.phDPosition","*.staff").rootName("people").transform(new DateTransformer("yyyy-MM-dd"), "updated").deepSerialize(peopleService.getAllPersons()), headers, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
  	
    }
 
	@PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    @RequestMapping(value="/people/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updatePerson(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
			PersonVO pVO = new JSONDeserializer<PersonVO>().use(null, PersonVO.class).deserialize(json);
			pVO.setId(id);
			pVO = peopleService.savePerson(pVO);
			
 			String restResponse = new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("people").deepSerialize(pVO);
			restResponse = new StringBuilder(restResponse).insert(1, "success: true,").toString();

            return new ResponseEntity<String>(restResponse, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

 
	@PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    @RequestMapping(value="/people", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createPerson(@RequestBody String json, UriComponentsBuilder uriBuilder) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
			PersonVO pVO = new JSONDeserializer<PersonVO>().use(null, PersonVO.class).use(Date.class, new DateTransformer("yyyy-MM-dd") ).deserialize(json);
			pVO = peopleService.savePerson(pVO);
            RequestMapping a = (RequestMapping) getClass().getAnnotation(RequestMapping.class);
            headers.add("Location",uriBuilder.path(a.value()[0]+"/"+pVO.getId().toString()).build().toUriString());

 			String restResponse = new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("people").deepSerialize(pVO);
			restResponse = new StringBuilder(restResponse).insert(1, "success: true,").toString();

            return new ResponseEntity<String>(restResponse, headers, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


	@PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
	@RequestMapping(value = "/people/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
	public ResponseEntity<String> deletePerson(@PathVariable("id") Long id) {
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
			peopleService.deletePerson(id);
            return new ResponseEntity<String>("{success: true, id : " +id.toString() + "}", headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


	/* Staff */
		
    @RequestMapping(value="/staff", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> allStaff() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        try {
 			return new ResponseEntity<String>(new JSONSerializer().prettyPrint(true).exclude("*.class","*.person","*.organisationUnit").rootName("staff").transform(new DateTransformer("yyyy-MM-dd"), "updated").serialize(peopleService.getAllStaff()), headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("allStaff got a pesky exception: "+ e + e.getCause());
			return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
  	
    }
 
	@PreAuthorize("hasRole('ROLE_DIRECTOROFSTUDIES')")
    @RequestMapping(value="/staff/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateStaff(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
			StaffVO sVO = new JSONDeserializer<StaffVO>().use(null, StaffVO.class).deserialize(json);
			sVO.setId(id);
			sVO = peopleService.saveStaff(sVO);
			
 			String restResponse = new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("staff").deepSerialize(sVO);
			restResponse = new StringBuilder(restResponse).insert(1, "success: true,").toString();

            return new ResponseEntity<String>(restResponse, headers, HttpStatus.OK);
        } catch (Exception e) {
			logger.error("updateStaff got a pesky exception: "+ e + e.getCause());
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

 
	@PreAuthorize("hasRole('ROLE_DIRECTOROFSTUDIES')")
    @RequestMapping(value="/staff", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createStaff(@RequestBody String json, UriComponentsBuilder uriBuilder) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
			logger.debug("createStaff, json "+ json);
			StaffVO sVO = new JSONDeserializer<StaffVO>().use(null, StaffVO.class).use(Date.class, new DateTransformer("yyyy-MM-dd") ).deserialize(json);
			logger.debug("createStaff, sVO "+ ReflectionToStringBuilder.toString(sVO, ToStringStyle.MULTI_LINE_STYLE));

			sVO = peopleService.saveStaff(sVO);
            RequestMapping a = (RequestMapping) getClass().getAnnotation(RequestMapping.class);
            headers.add("Location",uriBuilder.path(a.value()[0]+"/"+sVO.getId().toString()).build().toUriString());

 			String restResponse = new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("staff").deepSerialize(sVO);
			restResponse = new StringBuilder(restResponse).insert(1, "success: true,").toString();

            return new ResponseEntity<String>(restResponse, headers, HttpStatus.CREATED);
        } catch (Exception e) {
			logger.error("createStaff got a pesky exception: "+ e + e.getCause());
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


	@PreAuthorize("hasRole('ROLE_DIRECTOROFSTUDIES')")
	@RequestMapping(value = "/staff/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
	public ResponseEntity<String> deleteStaff(@PathVariable("id") Long id) {
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
			peopleService.deleteStaff(id);
            return new ResponseEntity<String>("{success: true, id : " +id.toString() + "}", headers, HttpStatus.OK);
        } catch (Exception e) {
			logger.error("deleteStaff got a pesky exception: "+ e + e.getCause());
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


	/* Curren user REST service */

	@Autowired
	UserRepo userRepo;
		
	@RequestMapping(value="/currentuser", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> loggedInUser(Principal principal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        try {
			logger.debug("loggedInUser... "+ ReflectionToStringBuilder.toString(principal, ToStringStyle.MULTI_LINE_STYLE));
			if (principal == null) { 
				// Dummy for testing purposes
     			return new ResponseEntity<String>(new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("currentuser").deepSerialize(new UserVO(userRepo.findUserByUsername("mikathol"))), headers, HttpStatus.OK);
			} else {
    			return new ResponseEntity<String>(new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("currentuser").deepSerialize(new UserVO(userRepo.findUserByUsername(principal.getName()))), headers, HttpStatus.OK);
			}
		} catch (Exception e) {
           return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
  	
    }



	/* Auxilliary components */
	
/* 
	private class ExtJSFormResult {
 
		private boolean success;
 
		public boolean isSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
 
		public String toString(){
			return "{success:"+this.success+"}";
		}
	}	
	
 */
	
} 
