package se.uu.ebc.bemanning.web;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
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
import se.uu.ebc.bemanning.repo.CourseInstanceRepo;
import se.uu.ebc.bemanning.repo.OrganisationUnitRepo;
import se.uu.ebc.bemanning.repo.StaffRepo;
import se.uu.ebc.bemanning.repo.PhDPositionRepo;
import se.uu.ebc.bemanning.security.UserRepo;
import se.uu.ebc.bemanning.security.BemanningUser;
import se.uu.ebc.bemanning.entity.CourseInstance;
import se.uu.ebc.bemanning.entity.Staff;
import se.uu.ebc.bemanning.entity.OrganisationUnit;

@Controller
@RequestMapping("/view")
@CrossOrigin(origins = "http://localhost:1841", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class AssignmentViewController {

    private static Logger logger = Logger.getLogger(AssignmentViewController.class.getName());

    private String roleArr[] = { "ROLE_DIRECTOROFSTUDIES", "ROLE_ADMINISTRATOR", "ROLE_PHDADMIN" };
    private Set<String> rolesForAll = new HashSet(Arrays.asList(roleArr));

	@Autowired
	TestService testService;

	@Autowired
	CourseInstanceRepo ciRepo;
	
	@Autowired
	StaffRepo staffRepo;
	
	@Autowired
	StaffingService staffingService;
	
	@Autowired
	UserRepo userRepo;

	@Autowired
	OrganisationUnitRepo ouRepo;
	
	@Autowired
	PhDService phdService;
	@Autowired
	PhDPositionRepo phdPositionRepo;
	
/* 
    @RequestMapping("/testen")
    @ResponseBody
    public ResponseEntity<String> allspecimen() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        try {
    		testService.test();
    	return new ResponseEntity<String>("{OK}", headers, HttpStatus.OK);
          } catch (Exception e) {
           return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
 */

    @RequestMapping(value = "/ViewByPerson", method = RequestMethod.GET)
    public String viewByPerson(@RequestParam(value = "year", required = false) String year, Model model, Principal principal, HttpServletRequest request) {
		try {
			logger.debug("MTh viewByPerson, year "+ year);
			logger.debug("MTh viewByPerson, model "+ReflectionToStringBuilder.toString(model, ToStringStyle.MULTI_LINE_STYLE));
			logger.debug("MTh viewByPerson, principal "+ReflectionToStringBuilder.toString(principal, ToStringStyle.MULTI_LINE_STYLE));

			String thisYear = year==null ? thisYear() : year;
			String budgetDept = staffingService.findUserByPersonAndYear(userRepo.findUserByUsername(principal.getName()), thisYear).getOrganisationUnit().getEconomyHolder(thisYear).getSvName();
//			Set<Staff> staff = staffRepo.findByYear(thisYear);
			List<Staff> staff;
			if (allowUserAll(request)) {
				staff = staffingService.getAssignedStaff(thisYear, staffingService.findUserByPersonAndYear(userRepo.findUserByUsername(principal.getName()), thisYear).getOrganisationUnit().getEconomyHolder(thisYear));
			} else {
				staff = new ArrayList<Staff>();
				staff.add(staffingService.findUserByPersonAndYear(userRepo.findUserByUsername(principal.getName()), thisYear));
			}
			Map<OrganisationUnit, List<Staff>> ous = new HashMap<OrganisationUnit, List<Staff>>();
			for (Staff s : staff) {
				if (!ous.containsKey(s.getOrganisationUnit())) {
					ous.put(s.getOrganisationUnit(),new ArrayList<Staff>());
				}
				ous.get(s.getOrganisationUnit()).add(s);
			}

			model.addAttribute("ous", ous);
			model.addAttribute("staff", staff);
			model.addAttribute("serverTime", new Date());
			model.addAttribute("budgetYear", new BudgetYear(thisYear, staffRepo.getStaffedYears()));
			model.addAttribute("budgetDept", budgetDept);
			model.addAttribute("year", thisYear);

			return "ViewByPerson";
        } catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("MTh viewByPerson, pesky exception "+e);
			}
           return "{\"ERROR\":"+e.getMessage()+"\"}";        
        }
	}
 
    @RequestMapping(value = "/ViewByPerson", method = RequestMethod.POST)
    public String viewByPersonPost(@ModelAttribute("year") String year, Model model, Principal principal, HttpServletRequest request) {
			logger.debug("MTh viewByPerson, POST year "+ year);
			logger.debug("MTh viewByPerson, POST model "+ReflectionToStringBuilder.toString(model, ToStringStyle.MULTI_LINE_STYLE));

			return viewByPerson(year,model, principal, request);
	}



    @RequestMapping(value = "/ViewByCourse", method = RequestMethod.GET)
    public String viewByCourse(@RequestParam(value = "year", required = false) String year, Model model, Principal principal, HttpServletRequest request) {
		try {
			logger.debug("MTh viewByCourse, year "+ year);
			logger.debug("MTh viewByCourse, model "+ReflectionToStringBuilder.toString(model, ToStringStyle.MULTI_LINE_STYLE));
			logger.debug("MTh viewByCourse, principal "+ReflectionToStringBuilder.toString(principal, ToStringStyle.MULTI_LINE_STYLE));

			String thisYear = year==null ? thisYear() : year;
			Staff staff = staffingService.findUserByPersonAndYear(userRepo.findUserByUsername(principal.getName()), thisYear);
			String budgetDept = staff.getOrganisationUnit().getEconomyHolder(thisYear).getSvName();
			
			List<CourseInstance> cis;
			if (allowUserAll(request)) {
				if (budgetDept != null) {
					cis = staffingService.getRelevantCourseAssignments(budgetDept,thisYear,staff.getOrganisationUnit().getExpandedOuAbbreviations(thisYear));
 				} else {
 					cis = staffingService.getCourseAssignments(thisYear);
 				}
			} else {
				cis = staffingService.getCourseAssignments(thisYear, staff);
			}
			

			logger.debug("MTh viewByCourse, staff "+ReflectionToStringBuilder.toString(staff, ToStringStyle.MULTI_LINE_STYLE));
			logger.debug("MTh viewByCourse, budgetYear "+ReflectionToStringBuilder.toString(new BudgetYear(thisYear, staffRepo.getStaffedYears()), ToStringStyle.MULTI_LINE_STYLE));
			logger.debug("MTh viewByCourse, budgetDept "+ReflectionToStringBuilder.toString(budgetDept, ToStringStyle.MULTI_LINE_STYLE));

			model.addAttribute("courses", cis);
			model.addAttribute("serverTime", new Date());
			model.addAttribute("budgetYear", new BudgetYear(thisYear, staffRepo.getStaffedYears()));
			model.addAttribute("budgetDept", budgetDept);
			model.addAttribute("year", thisYear);

			return "ViewByCourse";
        } catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("MTh viewByCourse, pesky exception "+e);
			}
           return "{\"ERROR\":"+e.getMessage()+"\"}";        
        }
	}
 
    @RequestMapping(value = "/ViewByCourse", method = RequestMethod.POST)
    public String viewByCoursePost(@ModelAttribute("year") String year, Model model, Principal principal, HttpServletRequest request) {
			if (logger.isDebugEnabled()) {
				logger.debug("MTh viewByCourse, POST year "+ year);
				logger.debug("MTh viewByCourse, POST model "+ReflectionToStringBuilder.toString(model, ToStringStyle.MULTI_LINE_STYLE));
			}
			return viewByCourse(year,model, principal, request);
	}




    @RequestMapping(value = "/ViewStaffSummary", method = RequestMethod.GET)
    public String viewStaffSummary(@RequestParam(value = "year", required = false) String year, @RequestParam(value = "ou", required = false) Long ouid, Model model, Principal principal, HttpServletRequest request) {
		try {
			logger.debug("MTh viewStaffSummary, year "+ year);
			logger.debug("MTh viewStaffSummary, model "+ReflectionToStringBuilder.toString(model, ToStringStyle.MULTI_LINE_STYLE));
			logger.debug("MTh viewStaffSummary, principal "+ReflectionToStringBuilder.toString(principal, ToStringStyle.MULTI_LINE_STYLE));

			String thisYear = year==null ? thisYear() : year;
			OrganisationUnit budgetDept = ouid == null ? 
				staffingService.findUserByPersonAndYear(userRepo.findUserByUsername(principal.getName()), thisYear).getOrganisationUnit().getEconomyHolder(thisYear) :
				ouRepo.findById(ouid);

			List<Staff> staff = staffingService.getAssignedStaff(thisYear,budgetDept);
						

			logger.debug("MTh viewStaffSummary, staff "+ReflectionToStringBuilder.toString(staff, ToStringStyle.MULTI_LINE_STYLE));
			logger.debug("MTh viewStaffSummary, budgetYear "+ReflectionToStringBuilder.toString(new BudgetYear(thisYear, staffRepo.getStaffedYears()), ToStringStyle.MULTI_LINE_STYLE));
			logger.debug("MTh viewStaffSummary, budgetDept "+budgetDept.getSvName());
//			logger.debug("MTh viewStaffSummary, budgetDept "+ReflectionToStringBuilder.toString(budgetDept, ToStringStyle.MULTI_LINE_STYLE));

			model.addAttribute("staff", staff);
			model.addAttribute("serverTime", new Date());
			model.addAttribute("budgetYear", new BudgetYear(thisYear, staffRepo.getStaffedYears()));
			model.addAttribute("budgetDept", budgetDept);
			model.addAttribute("activeDepts", ouRepo.findOusInSystem());
			model.addAttribute("year", thisYear);

			return "ViewStaffSummary";
        } catch (Exception e) {
			logger.error("MTh viewStaffSummary, pesky exception "+e);
        	return "{\"ERROR\":"+e.getMessage()+"\"}";        
        }
	}
 
    @RequestMapping(value = "/ViewStaffSummary", method = RequestMethod.POST)
    public String viewStaffSummaryPost(@ModelAttribute("year") String year, @ModelAttribute("ou") Long ouid, Model model, Principal principal, HttpServletRequest request) {
				logger.debug("MTh viewStaffSummary, POST year "+ year);
				logger.debug("MTh viewStaffSummary, POST ou "+ ouid);
				logger.debug("MTh viewStaffSummary, POST model "+ReflectionToStringBuilder.toString(model, ToStringStyle.MULTI_LINE_STYLE));
			return viewStaffSummary(year, ouid, model, principal, request);
	}




    @RequestMapping(value = "/ViewPhDProgress", method = RequestMethod.GET)
    public String viewProgressByPhD( Model model, Principal principal, HttpServletRequest request) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("MTh viewProgressByPhD, model "+ReflectionToStringBuilder.toString(model, ToStringStyle.MULTI_LINE_STYLE));
				logger.debug("MTh viewProgressByPhD, principal "+ReflectionToStringBuilder.toString(principal, ToStringStyle.MULTI_LINE_STYLE));
			}

			model.addAttribute("phdStudents", phdService.allSorted());
//			model.addAttribute("phdStudents", phdPositionRepo.findAll());
			model.addAttribute("serverTime", new Date());

			return "ViewPhDProgress";
        } catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("MTh viewProgressByPhD, pesky exception "+e);
			}
           return "{\"ERROR\":"+e.getMessage()+"\"}";        
        }
	}





    @RequestMapping(value = "/ViewCourseOverview", method = RequestMethod.GET)
    public String viewCourseOverview(@RequestParam(value = "year", required = false) String year, Model model, Principal principal, HttpServletRequest request) {
		try {
			logger.debug("MTh viewCourseOverview, year "+ year);
			logger.debug("MTh viewCourseOverview, model "+ReflectionToStringBuilder.toString(model, ToStringStyle.MULTI_LINE_STYLE));
			logger.debug("MTh viewCourseOverview, principal "+ReflectionToStringBuilder.toString(principal, ToStringStyle.MULTI_LINE_STYLE));

			String thisYear = year==null ? thisYear() : year;
			
			List<CourseInstance> cis= ciRepo.findByYear(thisYear);			

			logger.debug("MTh viewCourseOverview, courses "+ReflectionToStringBuilder.toString(cis, ToStringStyle.MULTI_LINE_STYLE));

			model.addAttribute("courses", cis);
			model.addAttribute("serverTime", new Date());
			model.addAttribute("budgetYear", new BudgetYear(thisYear, staffRepo.getStaffedYears()));
			model.addAttribute("year", thisYear);

			return "CoursesOverview";
        } catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("MTh viewByCourse, pesky exception "+e);
			}
           return "{\"ERROR\":"+e.getMessage()+"\"}";        
        }
	}
 
    @RequestMapping(value = "/ViewCourseOverview", method = RequestMethod.POST)
    public String viewCourseOverviewPost(@ModelAttribute("year") String year, Model model, Principal principal, HttpServletRequest request) {
			if (logger.isDebugEnabled()) {
				logger.debug("MTh viewByCourse, POST year "+ year);
				logger.debug("MTh viewByCourse, POST model "+ReflectionToStringBuilder.toString(model, ToStringStyle.MULTI_LINE_STYLE));
			}
			return viewCourseOverview(year,model, principal, request);
	}






	public class BudgetYear {
		private List<String> allYears;

		public List<String> getAllYears()
		{
			return this.allYears;
		}

		public void setAllYears(List<String> allYears)
		{
			this.allYears = allYears;
		}

		private String year;

		public String getYear()
		{
			return this.year;
		}

		public void setYear(String year)
		{
			this.year = year;
		}

		
		BudgetYear () {
			allYears = new ArrayList<String>();
			allYears.add("2017");
			allYears.add("2016");
			allYears.add("2015");
		}
		BudgetYear (String Year, List<String> allYears) {
			this.allYears = allYears;
			this.year=year;
		}
	}
	
	
	private boolean allowUserAll (HttpServletRequest request) throws Exception
	{
		boolean granted = false;
		try {
			for (String role : rolesForAll) {
				if (logger.isDebugEnabled()) {
					logger.debug("MTh allowUserAll, role "+role);
				}
				if (request.isUserInRole(role)) {
					granted = true;
					break;
				}
			}	
		} catch (Exception ex) {
 
			if (logger.isErrorEnabled()) {
				logger.error("MTh allowUserAll, pesky exception "+ex);
			}
 
		}
		
		return granted;
		
	}

	private String thisYear() {
		Calendar now = Calendar.getInstance();
		return Integer.toString((now.get(Calendar.YEAR)));
	}



	
}
