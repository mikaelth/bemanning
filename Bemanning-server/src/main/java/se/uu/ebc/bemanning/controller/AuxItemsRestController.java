package se.uu.ebc.bemanning.controller;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;

import se.uu.ebc.bemanning.enums.UserRoleType;
import se.uu.ebc.bemanning.enums.GrantType;
import se.uu.ebc.bemanning.enums.EmploymentType;
import se.uu.ebc.bemanning.enums.CourseGroup;
import se.uu.ebc.bemanning.repo.StaffRepo;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/rest")
@CrossOrigin(origins = "http://localhost:1841", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class AuxItemsRestController {


	@Autowired
	StaffRepo staffRepo;
	
	@GetMapping("/years")
    public ResponseEntity usedYears() {
    	List theList = new ArrayList<Map<String, String>>();
    	for (String s : staffRepo.getStaffedYears()) {
			java.util.Map<String,String> vMap = new java.util.HashMap<String, String>();
			vMap.put("label", s.toString());
    		theList.add(vMap);
    	}
    	return ResponseEntity.ok(theList);
    }


	@GetMapping("/userroletypes")
    public ResponseEntity userRoleTypes() {
    	List theList = new ArrayList<Map<String, UserRoleType>>();
    	for (UserRoleType s : UserRoleType.values()) {
			java.util.Map<String,UserRoleType> vMap = new java.util.HashMap<String, UserRoleType>();
			vMap.put("label", s);
    		theList.add(vMap);
    	}
    	return ResponseEntity.ok(theList);
    }

	@GetMapping("/employmenttypes")
    public ResponseEntity employmentType() {
    	List theList = new ArrayList<Map<String, EmploymentType>>();
    	for (EmploymentType s : EmploymentType.values()) {
			java.util.Map<String,EmploymentType> vMap = new java.util.HashMap<String, EmploymentType>();
			vMap.put("label", s);
    		theList.add(vMap);
    	}
    	return ResponseEntity.ok(theList);
    }

	@GetMapping("/granttypes")
    public ResponseEntity grantType() {
    	List theList = new ArrayList<Map<String, GrantType>>();
    	for (GrantType s : GrantType.values()) {
			java.util.Map<String,GrantType> vMap = new java.util.HashMap<String, GrantType>();
			vMap.put("label", s);
    		theList.add(vMap);
    	}
    	return ResponseEntity.ok(theList);
    }
	
	@GetMapping("/coursegroups")
    public ResponseEntity courseGroup() {
    	List theList = new ArrayList<Map<String, String>>();
    	for (CourseGroup s : CourseGroup.values()) {
			java.util.Map<String,String> vMap = new java.util.HashMap<String, String>();
			vMap.put("label", s.toString());
    		theList.add(vMap);
    	}
    	return ResponseEntity.ok(theList);
    }

} 
