package se.uu.ebc.bemanning.web;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.apache.log4j.Logger;

import se.uu.ebc.bemanning.service.TestService;
import se.uu.ebc.bemanning.service.PhDService;
import se.uu.ebc.bemanning.service.StaffingService;
import se.uu.ebc.bemanning.repo.StaffRepo;
import se.uu.ebc.bemanning.repo.PhDPositionRepo;
import se.uu.ebc.bemanning.security.UserRepo;
import se.uu.ebc.bemanning.security.BemanningUser;
import se.uu.ebc.bemanning.entity.CourseInstance;
import se.uu.ebc.bemanning.entity.Staff;
import se.uu.ebc.bemanning.entity.OrganisationUnit;

@Controller
@RequestMapping("/")
@CrossOrigin(origins = "http://localhost:1841", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class IndexViewController {

    private static Logger logger = Logger.getLogger(IndexViewController.class.getName());

    private String roleArr[] = { "ROLE_DIRECTOROFSTUDIES", "ROLE_ADMINISTRATOR", "ROLE_PHDADMIN" };
    private Set<String> rolesForAll = new HashSet(Arrays.asList(roleArr));

	@Autowired
	TestService testService;

	@Autowired
	StaffRepo staffRepo;
	
	@Autowired
	StaffingService staffingService;
	
	@Autowired
	UserRepo userRepo;

	@Autowired
	PhDService phdService;
	@Autowired
	PhDPositionRepo phdPositionRepo;
	
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String indexPage(Model model, Principal principal, HttpServletRequest request) {
			logger.debug("indexPage, model "+ReflectionToStringBuilder.toString(model, ToStringStyle.MULTI_LINE_STYLE));
			logger.debug("indexPage, principal "+ReflectionToStringBuilder.toString(principal, ToStringStyle.MULTI_LINE_STYLE));
 			logger.debug("indexPage, user "+ReflectionToStringBuilder.toString(userRepo.findUserByUsername(principal.getName()), ToStringStyle.MULTI_LINE_STYLE));

       try {
			model.addAttribute("user", userRepo.findUserByUsername(principal.getName()));
    		return "EntryPage";
        } catch (Exception e) {
			logger.error("indexPage, caught a pesky exception "+ e);
			return "{\"ERROR\":"+e.getMessage()+"\"}";
		}
	}

	
}
