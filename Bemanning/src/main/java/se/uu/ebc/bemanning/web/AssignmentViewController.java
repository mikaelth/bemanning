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

import se.uu.ebc.bemanning.service.AKKAService;
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
	AKKAService akkaService;

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
	


    @RequestMapping(value = "/ViewByPerson", method = RequestMethod.GET)
    public String viewByPerson(@RequestParam(value = "year", required = false) String year, Model model, Principal principal, HttpServletRequest request) {
		try {
			logger.debug("viewByPerson, year "+ year);
			logger.debug("viewByPerson, model "+ReflectionToStringBuilder.toString(model, ToStringStyle.MULTI_LINE_STYLE));
			logger.debug("viewByPerson, principal "+ReflectionToStringBuilder.toString(principal, ToStringStyle.MULTI_LINE_STYLE));

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
				logger.error("viewByPerson, pesky exception "+e);
			}
           return "{\"ERROR\":"+e.getMessage()+"\"}";        
        }
	}
 
    @RequestMapping(value = "/ViewByPerson", method = RequestMethod.POST)
    public String viewByPersonPost(@ModelAttribute("year") String year, Model model, Principal principal, HttpServletRequest request) {
			logger.debug("viewByPerson, POST year "+ year);
			logger.debug("viewByPerson, POST model "+ReflectionToStringBuilder.toString(model, ToStringStyle.MULTI_LINE_STYLE));

			return viewByPerson(year,model, principal, request);
	}



    @RequestMapping(value = "/ViewByCourse", method = RequestMethod.GET)
    public String viewByCourse(@RequestParam(value = "year", required = false) String year, Model model, Principal principal, HttpServletRequest request) {
		try {
			logger.debug("viewByCourse, year "+ year);
			logger.debug("viewByCourse, model "+ReflectionToStringBuilder.toString(model, ToStringStyle.MULTI_LINE_STYLE));
			logger.debug("viewByCourse, principal "+ReflectionToStringBuilder.toString(principal, ToStringStyle.MULTI_LINE_STYLE));

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
			

			logger.debug("viewByCourse, staff "+ReflectionToStringBuilder.toString(staff, ToStringStyle.MULTI_LINE_STYLE));
			logger.debug("viewByCourse, budgetYear "+ReflectionToStringBuilder.toString(new BudgetYear(thisYear, staffRepo.getStaffedYears()), ToStringStyle.MULTI_LINE_STYLE));
			logger.debug("viewByCourse, budgetDept "+ReflectionToStringBuilder.toString(budgetDept, ToStringStyle.MULTI_LINE_STYLE));

			model.addAttribute("courses", cis);
			model.addAttribute("serverTime", new Date());
			model.addAttribute("budgetYear", new BudgetYear(thisYear, staffRepo.getStaffedYears()));
			model.addAttribute("budgetDept", budgetDept);
			model.addAttribute("year", thisYear);

			return "ViewByCourse";
        } catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("viewByCourse, pesky exception "+e);
			}
           return "{\"ERROR\":"+e.getMessage()+"\"}";        
        }
	}
 
    @RequestMapping(value = "/ViewByCourse", method = RequestMethod.POST)
    public String viewByCoursePost(@ModelAttribute("year") String year, Model model, Principal principal, HttpServletRequest request) {
			if (logger.isDebugEnabled()) {
				logger.debug("viewByCourse, POST year "+ year);
				logger.debug("viewByCourse, POST model "+ReflectionToStringBuilder.toString(model, ToStringStyle.MULTI_LINE_STYLE));
			}
			return viewByCourse(year,model, principal, request);
	}




    @RequestMapping(value = "/ViewStaffSummary", method = RequestMethod.GET)
    public String viewStaffSummary(@RequestParam(value = "year", required = false) String year, @RequestParam(value = "ou", required = false) Long ouid, Model model, Principal principal, HttpServletRequest request) {
		try {
			logger.debug("viewStaffSummary, year "+ year);
			logger.debug("viewStaffSummary, model "+ReflectionToStringBuilder.toString(model, ToStringStyle.MULTI_LINE_STYLE));
			logger.debug("viewStaffSummary, principal "+ReflectionToStringBuilder.toString(principal, ToStringStyle.MULTI_LINE_STYLE));

			String thisYear = year==null ? thisYear() : year;
			OrganisationUnit budgetDept = ouid == null ? 
				staffingService.findUserByPersonAndYear(userRepo.findUserByUsername(principal.getName()), thisYear).getOrganisationUnit().getEconomyHolder(thisYear) :
				ouRepo.findById(ouid);

			List<Staff> staff = staffingService.getAssignedStaff(thisYear,budgetDept);
						

			logger.debug("viewStaffSummary, staff "+ReflectionToStringBuilder.toString(staff, ToStringStyle.MULTI_LINE_STYLE));
			logger.debug("viewStaffSummary, budgetYear "+ReflectionToStringBuilder.toString(new BudgetYear(thisYear, staffRepo.getStaffedYears()), ToStringStyle.MULTI_LINE_STYLE));
			logger.debug("viewStaffSummary, budgetDept "+budgetDept.getSvName());
//			logger.debug("viewStaffSummary, budgetDept "+ReflectionToStringBuilder.toString(budgetDept, ToStringStyle.MULTI_LINE_STYLE));

			model.addAttribute("akka", akkaService);
			
			model.addAttribute("staff", staff);
			model.addAttribute("serverTime", new Date());
			model.addAttribute("budgetYear", new BudgetYear(thisYear, staffRepo.getStaffedYears()));
			model.addAttribute("budgetDept", budgetDept);
			model.addAttribute("activeDepts", ouRepo.findOusInSystem());
			model.addAttribute("year", thisYear);

			return "ViewStaffSummary";
        } catch (Exception e) {
			logger.error("viewStaffSummary, pesky exception "+e);
        	return "{\"ERROR\":"+e.getMessage()+"\"}";        
        }
	}
 
    @RequestMapping(value = "/ViewStaffSummary", method = RequestMethod.POST)
    public String viewStaffSummaryPost(@ModelAttribute("year") String year, @ModelAttribute("ou") Long ouid, Model model, Principal principal, HttpServletRequest request) {
				logger.debug("viewStaffSummary, POST year "+ year);
				logger.debug("viewStaffSummary, POST ou "+ ouid);
				logger.debug("viewStaffSummary, POST model "+ReflectionToStringBuilder.toString(model, ToStringStyle.MULTI_LINE_STYLE));
			return viewStaffSummary(year, ouid, model, principal, request);
	}



    @RequestMapping(value = "/ViewPhDProgress", method = RequestMethod.GET)
    public String viewProgressByPhD( Model model, Principal principal, HttpServletRequest request) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("viewProgressByPhD, model "+ReflectionToStringBuilder.toString(model, ToStringStyle.MULTI_LINE_STYLE));
				logger.debug("viewProgressByPhD, principal "+ReflectionToStringBuilder.toString(principal, ToStringStyle.MULTI_LINE_STYLE));
			}

			model.addAttribute("phdStudents", phdService.allSorted());
//			model.addAttribute("phdStudents", phdPositionRepo.findAll());
			model.addAttribute("serverTime", new Date());

			return "ViewPhDProgress";
        } catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("viewProgressByPhD, pesky exception "+e);
			}
           return "{\"ERROR\":"+e.getMessage()+"\"}";        
        }
	}



    @RequestMapping(value = "/ViewCourseEconOverview", method = RequestMethod.GET)
    public String viewCourseEconOverview(@RequestParam(value = "year", required = false) String year, Model model, Principal principal, HttpServletRequest request) {
		try {
			logger.debug("viewCourseEconOverview, year "+ year);
			logger.debug("viewCourseEconOverview, model "+ReflectionToStringBuilder.toString(model, ToStringStyle.MULTI_LINE_STYLE));
			logger.debug("viewCourseEconOverview, principal "+ReflectionToStringBuilder.toString(principal, ToStringStyle.MULTI_LINE_STYLE));

			String thisYear = year==null ? thisYear() : year;
			OrganisationUnit budgetDept = staffingService.findUserByPersonAndYear(userRepo.findUserByUsername(principal.getName()), thisYear).getOrganisationUnit().getEconomyHolder(thisYear);
			
			List<CourseInstance> cis= ciRepo.findByYear(thisYear);			

			logger.debug("viewCourseEconOverview, courses "+ReflectionToStringBuilder.toString(cis, ToStringStyle.MULTI_LINE_STYLE));

			model.addAttribute("courses", cis);
			model.addAttribute("ou", budgetDept);
			model.addAttribute("serverTime", new Date());
			model.addAttribute("budgetYear", new BudgetYear(thisYear, staffRepo.getStaffedYears()));
			model.addAttribute("year", thisYear);

			return "CourseEconomyOverview";
        } catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("viewCourseEconOverview, pesky exception "+e);
			}
           return "{\"ERROR\":"+e.getMessage()+"\"}";        
        }
	}
 
    @RequestMapping(value = "/ViewCourseEconOverview", method = RequestMethod.POST)
    public String viewCourseEconOverviewPost(@ModelAttribute("year") String year, Model model, Principal principal, HttpServletRequest request) {
			if (logger.isDebugEnabled()) {
				logger.debug("viewByCourse, POST year "+ year);
				logger.debug("viewByCourse, POST model "+ReflectionToStringBuilder.toString(model, ToStringStyle.MULTI_LINE_STYLE));
			}
			return viewCourseEconOverview(year,model, principal, request);
	}




    @RequestMapping(value = "/ViewCourseOverview", method = RequestMethod.GET)
    public String viewCourseOverview(@RequestParam(value = "year", required = false) String year, Model model, Principal principal, HttpServletRequest request) {
		try {
			logger.debug("viewCourseOverview, year "+ year);
			logger.debug("viewCourseOverview, model "+ReflectionToStringBuilder.toString(model, ToStringStyle.MULTI_LINE_STYLE));
			logger.debug("viewCourseOverview, principal "+ReflectionToStringBuilder.toString(principal, ToStringStyle.MULTI_LINE_STYLE));

			String thisYear = year==null ? thisYear() : year;
			
			List<CourseInstance> cis= ciRepo.findByYear(thisYear);			

			logger.debug("viewCourseOverview, courses "+ReflectionToStringBuilder.toString(cis, ToStringStyle.MULTI_LINE_STYLE));

			model.addAttribute("akka", akkaService);
			model.addAttribute("courses", cis);
			model.addAttribute("serverTime", new Date());
			model.addAttribute("budgetYear", new BudgetYear(thisYear, staffRepo.getStaffedYears()));
			model.addAttribute("year", thisYear);

			return "CoursesOverview";
        } catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("viewCourseOverview, pesky exception "+e);
			}
           return "{\"ERROR\":"+e.getMessage()+"\"}";        
        }
	}
 
    @RequestMapping(value = "/ViewCourseOverview", method = RequestMethod.POST)
    public String viewCourseOverviewPost(@ModelAttribute("year") String year, Model model, Principal principal, HttpServletRequest request) {
			if (logger.isDebugEnabled()) {
				logger.debug("viewByCourse, POST year "+ year);
				logger.debug("viewByCourse, POST model "+ReflectionToStringBuilder.toString(model, ToStringStyle.MULTI_LINE_STYLE));
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
					logger.debug("allowUserAll, role "+role);
				}
				if (request.isUserInRole(role)) {
					granted = true;
					break;
				}
			}	
		} catch (Exception ex) {
 
			if (logger.isErrorEnabled()) {
				logger.error("allowUserAll, pesky exception "+ex);
			}
 
		}
		
		return granted;
		
	}

	private String thisYear() {
		Calendar now = Calendar.getInstance();
		return Integer.toString((now.get(Calendar.YEAR)));
	}



	
}
