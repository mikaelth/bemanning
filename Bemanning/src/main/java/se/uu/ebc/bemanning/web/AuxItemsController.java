package se.uu.ebc.bemanning.web;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

import se.uu.ebc.bemanning.enums.UserRoleType;
import se.uu.ebc.bemanning.enums.GrantType;
import se.uu.ebc.bemanning.enums.EmploymentType;
import se.uu.ebc.bemanning.enums.CourseGroup;

import org.apache.log4j.Logger;

@RestController
@RequestMapping(value = "/rest")
@CrossOrigin(origins = "http://localhost:1841", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class AuxItemsController {

    private static Logger logger = Logger.getLogger(AuxItemsController.class.getName());


	@RequestMapping("/userroletypes")
    public List<Map<String, UserRoleType>> userRoleTypes() {
    	List theList = new ArrayList<Map<String, UserRoleType>>();
    	for (UserRoleType s : UserRoleType.values()) {
			java.util.Map<String,UserRoleType> vMap = new java.util.HashMap<String, UserRoleType>();
			vMap.put("label", s);
    		theList.add(vMap);
    	}
    	return theList;
    }

	@RequestMapping("/employmenttypes")
    public List<Map<String, EmploymentType>> employmentType() {
    	List theList = new ArrayList<Map<String, EmploymentType>>();
    	for (EmploymentType s : EmploymentType.values()) {
			java.util.Map<String,EmploymentType> vMap = new java.util.HashMap<String, EmploymentType>();
			vMap.put("label", s);
    		theList.add(vMap);
    	}
    	return theList;
    }

	@RequestMapping("/granttypes")
    public List<Map<String, GrantType>> grantType() {
    	List theList = new ArrayList<Map<String, GrantType>>();
    	for (GrantType s : GrantType.values()) {
			java.util.Map<String,GrantType> vMap = new java.util.HashMap<String, GrantType>();
			vMap.put("label", s);
    		theList.add(vMap);
    	}
    	return theList;
    }
	
/* 
	@RequestMapping("/coursegroups")
    public List<Map<String, String>> courseGroup() {
    	List theList = new ArrayList<Map<String, String>>();
    	for (CourseGroup s : CourseGroup.values()) {
			java.util.Map<String,String> vMap = new java.util.HashMap<String, String>();
			vMap.put("name", s.name());
			vMap.put("displayname", s.toString());
    		theList.add(vMap);
    	}
    	return theList;
    }
 */

	@RequestMapping("/coursegroups")
    public List<Map<String, String>> courseGroup() {
    	List theList = new ArrayList<Map<String, String>>();
    	for (CourseGroup s : CourseGroup.values()) {
			java.util.Map<String,String> vMap = new java.util.HashMap<String, String>();
			vMap.put("label", s.toString());
    		theList.add(vMap);
    	}
    	return theList;
    }

} 
