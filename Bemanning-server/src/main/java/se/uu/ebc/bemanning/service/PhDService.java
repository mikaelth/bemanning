package se.uu.ebc.bemanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Calendar;
import java.util.Comparator;

import java.time.Year;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.annotation.PostConstruct;
import se.uu.ebc.bemanning.entity.Person;
import se.uu.ebc.bemanning.entity.Progress;
import se.uu.ebc.bemanning.entity.staff.Staff;
import se.uu.ebc.bemanning.dto.PhDPositionDTO;
import se.uu.ebc.bemanning.dto.ProgressDTO;
import se.uu.ebc.bemanning.entity.PhDPosition;
import se.uu.ebc.bemanning.repo.PhDPositionRepo;
import se.uu.ebc.bemanning.repo.ProgressRepo;
import se.uu.ebc.bemanning.repo.PersonRepo;

//import se.uu.ebc.bemanning.mapper.PhDPositionMapper;
//import se.uu.ebc.bemanning.mapper.ProgressMapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.Converter;
import org.modelmapper.AbstractConverter;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.dao.OptimisticLockingFailureException;


@Slf4j
@Service
public class PhDService {

//	private final PhDPositionMapper phdMapper;
//	private final ProgressMapper progressMapper;
	private final PhDPositionRepo phdPositionRepo;
	private final ProgressRepo progressRepo;
	private final PersonRepo personRepo;
	private final StaffService staffService;

	/* Constructor injection */
	public PhDService (
		PhDPositionRepo phdPositionRepo,
		ProgressRepo progressRepo,
		PersonRepo personRepo,
		StaffService staffService) {

		this.phdPositionRepo = phdPositionRepo;
		this.progressRepo = progressRepo;
		this.personRepo = personRepo;
		this.staffService = staffService;
	}

	/*
	private ModelMapper progressModelMapper = new ModelMapper();

	private ModelMapper mapper = new ModelMapper();

	private TypeMap<PhDPosition, PhDPositionDTO> phdToVOMapper = mapper.createTypeMap(PhDPosition.class, PhDPositionDTO.class);
	private TypeMap<PhDPositionDTO,PhDPosition> voTophdMapper = mapper.createTypeMap(PhDPositionDTO.class, PhDPosition.class);

	public Set<Staff> getAllRelevantStaff(OrganisationUnit dept, String year) throws Exception {
		return staffRepo.getRelevantStaff(dept,year);
	}

 	public List<Staff> getAssignedStaff (String year, OrganisationUnit dept) {

		return staffRepo.findUserByOuListAndYear(dept.getExpandedOu(year),year);

 	}




	@PostConstruct
	public void init () {


		mapper.addConverter(isoLocalDateTimeConverter);


 		phdToVOMapper.addMapping(src -> src.getPerson().getId(), PhDPositionDTO::setPersonId);
 //		phdToVOMapper.addMapping(src -> src.getStart().toString(), PhDPositionDTO::setStart);
//  		phdToVOMapper.addMapping(PhDPosition::predictedFinishDate, PhDPositionDTO::setPredictedFinishDate);
//  		phdToVOMapper.addMapping(PhDPosition::predictedHalfTime, PhDPositionDTO::setPredictedHalfTime);
//  		phdToVOMapper.addMapping(PhDPosition::predicted80Percent, PhDPositionDTO::setPredicted80Percent);
//  		phdToVOMapper.addMapping(PhDPosition::currentRemainingProjectTime, PhDPositionDTO::setCurrentRemainingProjectTime);
// 		phdToVOMapper.addMapping(PhDPosition::getStart, PhDPositionDTO::setStart);

		phdToVOMapper.addMappings(mapper -> mapper.skip(PhDPositionDTO::setProgram));

	}

 */


 	public List<PhDPosition> allSorted () {
		List<PhDPosition> phds = new ArrayList<PhDPosition>();
		try {
			phds.addAll(phdPositionRepo.findAll());
			Collections.sort(phds, new CompPhDPositions());
		} catch (Exception e) {
				log.error("MTh allSorted, pesky exception "+e);
		} finally {

 			return phds;
 		}
 	}

	private class CompPhDPositions implements Comparator<PhDPosition>{

		public int compare(PhDPosition e1, PhDPosition e2) {

			int reply = 0;

			Staff s1 = staffService.findUserByPersonAndYear(e1.getPerson(), String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
			String ou1 = s1 != null ? (s1.getOrganisationUnit() != null ? s1.getOrganisationUnit().getSvName() : "" ) : "";
			Staff s2 = staffService.findUserByPersonAndYear(e2.getPerson(), String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
			String ou2 = s2 != null ? (s2.getOrganisationUnit() != null ? s2.getOrganisationUnit().getSvName() : "" ) : "";

			if (ou1 == ou2) {
				reply = e1.getPerson().getFamilyName().compareTo(e2.getPerson().getFamilyName());
			} else {
				reply = ou1.compareTo(ou2);
			}


			return reply;
		}
	}

/*
	Converter<String, LocalDateTime> isoLocalDateTimeConverter = new AbstractConverter<String, LocalDateTime>() {
    	private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

	    @Override
	    protected LocalDateTime convert(String source) {
 	       return source == null ? null : LocalDateTime.parse(source, formatter);
 	   }
	};
 */

	/* PhD Positions */

	public List<PhDPosition> getAllPhDPositions() throws ResourceNotFoundException {
 		String year = String.valueOf(Year.now().getValue());
		List<PhDPosition> pVOs = phdPositionRepo.findAll();

	 	return pVOs;

    }

	public PhDPosition getPhDById (Long id) {
		log.debug("getById()");
		PhDPosition p = phdPositionRepo.findById(id).orElseThrow();
		log.debug(p.toString());
		return p;

	}

    public PhDPosition savePhDPosition(PhDPosition p) throws Exception {

		phdPositionRepo.save(p);

    	if (p.getProgresses().size() == 0) {
			p.getProgresses().add(creatInitialProgress(p));
     		phdPositionRepo.save(p);
   		}
		return p;

    }

	private Progress creatInitialProgress (PhDPosition p) {
		Progress prog = new Progress();
			prog.setPhdPosition(p);
			prog.setDate(p.getStart());
			prog.setActivity(1.0f);
			prog.setProjectFraction(0.9f);
			prog.setGuFraction(0.1f);
			prog.setRemainingMonths(48.0f);
 		return prog;
   	}


    public synchronized void deletePhDPosition(Long pID) throws IllegalArgumentException, OptimisticLockingFailureException {
		phdPositionRepo.deleteById(pID);
    }


	public String findCurrentAffiliation (Person person, String year) {

		log.debug("findCurrentAffiliation by {} and {}",person.getName(),year);
		if (staffService.findOuByPersonAndYear(person, year).isPresent()) {
			return staffService.findOuByPersonAndYear(person, year).get().getSvName();
		} else {
			return "";
		}
	}
/*
	private PhDPosition toPhDPosition (PhDPositionDTO pvo) throws Exception {
		return phdMapper.dtoToEntity(pvo);
//		return toPhDPosition (new PhDPosition(),pvo);
   	}

	private PhDPosition toPhDPosition (PhDPosition p, PhDPositionDTO pvo) throws Exception {
//		mapper.map(pvo,p);
		phdMapper.updateEntityFromDTO(pvo,p);
		return p;
	}
 */



	/* Progresses */

	public List<Progress> getAllProgress() throws ResourceNotFoundException  {
		List<Progress> pVO = progressRepo.findAll();
        return pVO;
    }

    public Progress saveProgress(Progress p) throws Exception {
    	progressRepo.save(p);
		return p;
    }

    public synchronized void deleteProgress(Long pID) throws IllegalArgumentException, OptimisticLockingFailureException {
		progressRepo.deleteById(pID);
    }

/*
	private Progress toProgress (ProgressDTO pvo) throws Exception {
 		return toProgress (new Progress(), pvo);
   	}

	private Progress toProgress (Progress p, ProgressDTO pvo) throws Exception {
//		progressModelMapper.map(pvo,p);
		progressMapper.updateEntityFromDTO(pvo,p);
		p.setPhdPosition(phdPositionRepo.findById(pvo.getPhdPositionId()).get());

		return p;

	}
 */


}
