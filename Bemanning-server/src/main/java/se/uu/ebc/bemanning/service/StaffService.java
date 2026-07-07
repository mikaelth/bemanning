package se.uu.ebc.bemanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.time.Instant;
import java.time.Duration;
import java.util.stream.Collectors;
import java.util.Optional;
/*
import se.uu.ebc.bemanning.vo.PersonVO;
import se.uu.ebc.bemanning.entity.Person;
import se.uu.ebc.bemanning.repo.PersonRepo;
 */

import se.uu.ebc.bemanning.entity.Person;
import se.uu.ebc.bemanning.entity.staff.AkkaStaff;
import se.uu.ebc.bemanning.entity.staff.ExternalStaff;
import se.uu.ebc.bemanning.entity.staff.Staff;
import se.uu.ebc.bemanning.dto.StaffDTO;
import se.uu.ebc.bemanning.entity.MaxCost;
import se.uu.ebc.bemanning.entity.OrganisationUnit;

import se.uu.ebc.bemanning.enums.UserRoles;
import se.uu.ebc.bemanning.enums.EmploymentType;
import se.uu.ebc.bemanning.enums.StaffKind;

import se.uu.ebc.bemanning.repo.StaffRepo;
import se.uu.ebc.bemanning.repo.AkkaStaffRepo;
import se.uu.ebc.bemanning.repo.OrganisationUnitRepo;
import se.uu.ebc.bemanning.repo.MaxCostRepo;
import se.uu.ebc.bemanning.security.UserRepo;
import se.uu.ebc.bemanning.ldap.repository.StaffAkkaRepository;
import se.uu.ebc.bemanning.ldap.model.StaffAkka;

import org.modelmapper.ModelMapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;

@Slf4j
@Service
public class StaffService {


	@Value("${bemanning.top.ouid}")
	private Long defaultEcoHolder;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private StaffAkkaRepository uuStaffRepo;

	private ModelMapper modelMapper = new ModelMapper();

	@Autowired
	private StaffRepo staffRepo;

	@Autowired
	private AkkaStaffRepo akkaStaffRepo;

	@Autowired
	private OrganisationUnitRepo ouRepo;

/*	@Autowired
	private MaxCostRepo mcRepo;

 */


	/* Staff */

	public List<StaffDTO> getAllStaff() throws ResourceNotFoundException  {
		List<StaffDTO> sVO = new ArrayList<StaffDTO>();
			log.debug("getAllStaff()");
			for (Staff s : staffRepo.findAll()) {
 				sVO.add(modelMapper.map(s, StaffDTO.class));

 			}
         	return sVO;

    }

	public StaffDTO getById (Long id) {
		log.debug("getById()");
		Staff s = staffRepo.findById(id).get();
		log.debug(s.toString());
		return modelMapper.map(s, StaffDTO.class);
//		return new StaffVO(s);
	}

	public StaffDTO saveStaff(StaffDTO svo) throws Exception {
    	Staff s = svo.getId() == null ? toStaff(svo) : toStaff(staffRepo.findById(svo.getId()).get(), svo);
    	staffRepo.save(s);
 		return modelMapper.map(s, StaffDTO.class);

    }

	private Staff toStaff (StaffDTO svo) throws Exception {
		return switch (svo.getStaffKind()) {
 			case StaffKind.AKKA -> toStaff (new AkkaStaff(), svo);
 			case StaffKind.EXTERNAL -> toStaff (new ExternalStaff(), svo);
 			default -> throw new IllegalArgumentException ("No such staff kind");
 		};
   	}

	private Staff toStaff (Staff s, StaffDTO svo) throws Exception {
		modelMapper.map(svo, s);
		return s;
	}

	public synchronized void deleteStaff(Long sID) throws IllegalArgumentException, OptimisticLockingFailureException {
		staffRepo.deleteById(sID);
		return;
    }


  	public Staff findUserByPersonAndYear(Person person, String year) {

 		List<Staff> staff = staffRepo.findUserByPersonAndYear(person,year);
 		return staff.size() > 0 ? staff.get(0) : null;
 	}

  	public Optional<Staff> findStaffByPersonAndYear(Person person, String year) {
 		return Optional.ofNullable(findUserByPersonAndYear(person,year));
 	}


	/* Staff */

/*

	public List<StaffVO> getAllStaff() throws Exception {
		List<StaffVO> svo = new ArrayList<StaffVO>();
		try {
logger.debug("getAllStaff, begin findAll");
Instant start = Instant.now();
staffRepo.findAll();
Instant end = Instant.now();
logger.debug("getAllStaff, done findAll, took " + Duration.between(start,end));

			for (Staff s : staffRepo.findAll()) {
 				svo.add(new StaffVO(s));
// 				svo.add(new StaffVO(s,0.0f));
 			}
end = Instant.now();
logger.debug("getAllStaff, done findAll, took " + Duration.between(start,end));
         	return svo;
        } catch (Exception e) {

			logger.debug("getAllStaff caught a pesky exception, " + e);
			return null;

        }
    }

    public StaffVO saveStaff(StaffVO svo) throws Exception {
    	Staff s = svo.getId() == null ? toStaff(svo) : toStaff(staffRepo.findById(svo.getId()), svo);
    	staffRepo.save(s);
//		return new StaffVO(s,getPreviousUb(s));
		logger.error("saveStaff saved staff: "+ s);
		return new StaffVO(s);

    }

    public synchronized void deleteStaff(Long id) throws Exception {
		Staff s = staffRepo.findById(id);
		staffRepo.delete(s);
    }


	private Staff toStaff (StaffVO svo) throws Exception {
 		return toStaff (new Staff(), svo);
   	}

	private Staff toStaff (Staff s, StaffVO vo) throws Exception {
		try {
			s.setId(vo.getId());

			s.setPerson(personRepo.findById(vo.getPersonId()));
			s.setOrganisationUnit(ouRepo.findById(vo.getOrganisationUnitId()));

			s.setPercentGU(vo.getPercentGU());

			s.setPosition(vo.getPosition());
			s.setHourlyCharge(vo.getHourlyCharge());
			s.setYear(vo.getYear());
			s.setNote(vo.getNote());
			s.setIb(vo.getIb());

			MaxCost m = mcRepo.findByCategoryYear(s.getPosition(), s.getYear());
			if (m == null){
				m = new MaxCost(s.getPosition(), s.getYear());
				mcRepo.save(m);
			}
			s.setMaxCost(m);

		} catch (Exception e) {
			logger.error("toStaff got a pesky exception: "+ e + e.getCause());
		} finally {
			return s;
		}
	}
 */

/*
    private Float getPreviousUb(Staff s) {
    	Float ub = 0.0f;
		try {
    		Staff os = staffRepo.findUserByPersonYearAndPosition(s.getPerson(), Integer.toString(Integer.parseInt( s.getYear())-1), s.getPosition());

			logger.debug("getPreviousUb; s " + s + "; os " + os);

    		if (os != null) {
    			ub = os.getUb();
    		}

		} catch (Exception e) {
			logger.error("getPreviousUb got a pesky exception " + e);
		}
		finally {
			return ub;
		}
    }
 */


	public List<Staff> getAssignedStaff (String year, OrganisationUnit dept)
	{
		return staffRepo.findUserByOuListAndYear(dept.getExpandedOu(year),year);
 	}

	public StaffingRecord getAssignedStaff (String year)
	{
		List<Staff> staffList = new ArrayList<Staff>();
		OrganisationUnit ecoHolder = ouRepo.findById(defaultEcoHolder).get();
		OrganisationUnit ou = ouRepo.findById(defaultEcoHolder).get();

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("auth, {}",auth);
		String uName = auth.getName();
		Person p = userRepo.findUserByUsername(uName);
//		Person p = userRepo.findUserByUsername("mikathol");
		Optional<Staff> s = this.findStaffByPersonAndYear(p, year);
		if(s.isPresent()) {
			ou = s.get().getOrganisationUnit();
			ecoHolder = ou.getEconomyHolder(year);
		}


		if (auth != null && auth.isAuthenticated()) {
			if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(UserRoles.CoreDataAdmin.toString().toUpperCase()))) {
				staffList = staffRepo.findByYear(year);
				log.debug("getAssignedStaff, CoreDataAdmin, got {} staff", staffList.size());
			} else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(UserRoles.DirectorOfStudies.toString().toUpperCase()))) {
				staffList = this.getAssignedStaff( year, ecoHolder );
				log.debug("getAssignedStaff, DirectorOfStudies, got {} staff", staffList.size());
			} else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(UserRoles.ProgrammeHead.toString().toUpperCase()))) {
				staffList = this.getAssignedStaff( year, ou );
				log.debug("getAssignedStaff, Programme head, got {} staff", staffList.size());
			} else {
				staffList = staffRepo.findUserByPersonAndYear(p, year);
				log.debug("getAssignedStaff, Staff, got {} staff", staffList.size());
			}
		}
		/* Testing */
// 		staffList = staffRepo.findByYear("2026");
// 		log.debug("Staff list {}",staffList);

		Map<OrganisationUnit, List<Staff>> ous = staffList.stream()
			.collect(Collectors.groupingBy(Staff::getOrganisationUnit));


			return new StaffingRecord (ecoHolder, ous, staffList);

 	}

	public String updateEmpolyeeNumber() {
		log.debug("updateEmpolyeeNumber");
		for (AkkaStaff s : akkaStaffRepo.findAll()) {
//			log.debug("Updated staff {}",s);
			List<StaffAkka> as = uuStaffRepo.findByUsername(s.getPerson().getUsername());
			if (as.size() == 1) {
				s.setEmployeeNumber(as.get(0).getEmployeeNumber());
				akkaStaffRepo.save(s);
//				log.debug("Updated staff {}, username {}, {}",s.getEmployeeNumber(), s.getPerson().getUsername(), s.getPerson().getName());
			} else if (as.size() > 1) {
				log.debug("Not updated, ambiguous staff {}, {}, {} hits",s.getPerson().getUsername(), s.getPerson().getName(),as.size());
			}
		}
		return "Okelidokeli";
	}
}
