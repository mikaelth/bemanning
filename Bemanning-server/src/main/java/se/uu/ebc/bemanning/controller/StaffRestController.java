package se.uu.ebc.bemanning.controller;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import lombok.extern.slf4j.Slf4j;

import se.uu.ebc.bemanning.repo.StaffRepo;
import se.uu.ebc.bemanning.dto.StaffDTO;
import se.uu.ebc.bemanning.entity.staff.Staff;
import se.uu.ebc.bemanning.enums.UserRoles;
import se.uu.ebc.bemanning.service.StaffService;

import java.util.List;
import java.util.HashSet;
import java.util.HashMap;
import java.time.Year;

@RestController
@RequestMapping(value = "/rest")
@CrossOrigin(origins = {"http://localhost:1962","http://localhost:1841"})
@Slf4j
public class StaffRestController {


	@Autowired
	StaffRepo staffRepo;

	@Autowired
	StaffService staffService;


	private record DeleteStatus (Boolean sucess, Long id) {}
	private record CreateStaffStatus (Boolean sucess, StaffDTO staff) {}
	private record CreateStaffListStatus (Boolean sucess, List<StaffDTO> staff) {}

	/* Persons */
		
    @GetMapping(value="/staff")
    public ResponseEntity getAllEntities() {
		return ResponseEntity.ok(new CreateStaffListStatus (true, staffService.getAllStaff() )); 
    }

    @GetMapping(value="/staff/{id}")
    public StaffDTO getEntity(@PathVariable Long id) {
		return staffService.getById(id); 
    }
   
	@PreAuthorize("hasRole('ROLE_DIRECTOROFSTUDIES')")
    @PostMapping(value="/staff")
	public ResponseEntity createEntity(@RequestBody StaffDTO sVO) throws Exception {
		StaffDTO nsVO = staffService.saveStaff(sVO);
		return ResponseEntity.ok(new CreateStaffStatus(true,nsVO));
	}

	@PreAuthorize("hasRole('ROLE_DIRECTOROFSTUDIES')")
    @PutMapping(value="/staff/{id}")
    public ResponseEntity updateEntity(@RequestBody StaffDTO sVO, @PathVariable Long id) throws Exception {
		if (sVO.getId().equals(id)) {
			StaffDTO nsVO = staffService.saveStaff(sVO);
			return ResponseEntity.ok(new CreateStaffStatus(true,nsVO));		
		} else {
			throw ( new IllegalArgumentException() );
		} 			
    }


	@PreAuthorize("hasRole('ROLE_DIRECTOROFSTUDIES')")
	@DeleteMapping(value = "/staff/{id}")
	public ResponseEntity deleteEntity(@PathVariable Long id) {
		staffService.deleteStaff(id);
		return ResponseEntity.ok(new DeleteStatus(true,id));
    }

 
/* 
 
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
 */


	/* Staff */
		
/* 
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
 */

	
} 
