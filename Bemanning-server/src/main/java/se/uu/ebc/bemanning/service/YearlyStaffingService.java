package se.uu.ebc.bemanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.dao.OptimisticLockingFailureException;

import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import se.uu.ebc.luntan.vo.CourseInstanceVO;

import se.uu.ebc.bemanning.entity.YearlyStaffing;
import se.uu.ebc.bemanning.vo.YearlyStaffingVO;
import se.uu.ebc.bemanning.repo.YearlyStaffingRepo;


import lombok.extern.slf4j.Slf4j;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;

@Slf4j
@Service
public class YearlyStaffingService {

    // Spring constructor dependency injection!
    private final YearlyStaffingRepo yearlyStaffingRepo;
    public YearlyStaffingService(YearlyStaffingRepo yearlyStaffingRepo) {
    	this.yearlyStaffingRepo = yearlyStaffingRepo;
    }
    

	private ModelMapper modelMapper = new ModelMapper();

	/* YSP */
	
	public List<YearlyStaffingVO> getAllYearlyStaffings() throws ResourceNotFoundException  {
		List<YearlyStaffingVO> pVO = new ArrayList<YearlyStaffingVO>();
			log.debug("getAllYearlyStaffings()");
			for (YearlyStaffing p : yearlyStaffingRepo.findAll()) {
 				pVO.add(modelMapper.map(p, YearlyStaffingVO.class));
  			}
         	return pVO;        	        
    }

	public YearlyStaffingVO getById (Long id) {
		YearlyStaffing p = yearlyStaffingRepo.findById(id).get();
		return modelMapper.map(p, YearlyStaffingVO.class);
	}   
		
	public YearlyStaffingVO saveYearlyStaffing(YearlyStaffingVO pvo) throws Exception {
    	YearlyStaffing p = pvo.getId() == null ? toYearlyStaffing(pvo) : toYearlyStaffing(yearlyStaffingRepo.findById(pvo.getId()).get(), pvo);
    	yearlyStaffingRepo.save(p);
 		return modelMapper.map(p, YearlyStaffingVO.class);
   
    }

	private YearlyStaffing toYearlyStaffing (YearlyStaffingVO pvo) throws Exception {
 		return toYearlyStaffing (new YearlyStaffing(), pvo);
   	}

	private YearlyStaffing toYearlyStaffing (YearlyStaffing p, YearlyStaffingVO pvo) throws Exception {
		modelMapper.map(pvo, p);
		return p;
	}
    
	public synchronized void deleteYearlyStaffing(Long pID) throws IllegalArgumentException, OptimisticLockingFailureException {
		yearlyStaffingRepo.deleteById(pID);
		return;
    }


}
