package se.uu.ebc.bemanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import se.uu.ebc.bemanning.entity.OrganisationUnit;
import se.uu.ebc.bemanning.entity.YearsOfHierarchy;

import se.uu.ebc.bemanning.repo.OrganisationUnitRepo;
import se.uu.ebc.bemanning.repo.YearsOfHierarchyRepo;


import lombok.extern.slf4j.Slf4j;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;

@Slf4j
@Service
public class OrganisationUnitService {

	private final OrganisationUnitRepo ouRepo;
	private final YearsOfHierarchyRepo yohRepo;
	
	public OrganisationUnitService (OrganisationUnitRepo ouRepo, YearsOfHierarchyRepo yohRepo) {
		this.ouRepo = ouRepo;
		this.yohRepo = yohRepo;
	}

	/* Organisation Units */
	
	public List<OrganisationUnit> getAllOrganisationUnits() throws Exception {
		List<OrganisationUnit> ouVO = ouRepo.findAll();
		return ouVO;		
    }
    
    public OrganisationUnit saveOrganisationUnit(OrganisationUnit ou) throws Exception {
    	ouRepo.save(ou);
		return ou;
    
    }


    public synchronized void deleteOrganisationUnit(Long cID) throws Exception {
		ouRepo.deleteById(cID);
    }

	public synchronized void deleteOrganisationUnit(OrganisationUnit ou) throws Exception {
		ouRepo.delete(ou);
    }
  	

    /* Years of Hierarchy */
    
	public List<YearsOfHierarchy> getAllYearsOfHierarchy() throws Exception {
		List<YearsOfHierarchy> yohVO = yohRepo.findAll();
		return yohVO;        	        
    }
    
    public YearsOfHierarchy saveYearsOfHierarchy(YearsOfHierarchy yoh) throws Exception {
    	yohRepo.save(yoh);
		return yoh;
    
    }


    public synchronized void deleteYearsOfHierarchy(Long id) throws Exception {
		yohRepo.deleteById(id);
    }

	public synchronized void deleteYearsOfHierarchy(YearsOfHierarchy yoh) throws Exception {
		yohRepo.delete(yoh);
    }
 	
 
/* 
	private YearsOfHierarchy toYearsOfHierarchy (YoHVO yohVO) throws Exception {
 		return toYearsOfHierarchy (new YearsOfHierarchy(), yohVO);
   	}

	private YearsOfHierarchy toYearsOfHierarchy (YearsOfHierarchy entity, YoHVO vo) throws Exception {


		try {

			entity.setId(vo.getId());
			entity.setFirstYear(vo.getFirstYear());
			entity.setLastYear(vo.getLastYear());
//			entity.setLastYear(vo.getLastYear()==0 ? null : vo.getLastYear());

			entity.setNote(vo.getNote());
			
			entity.setSuperUnit(ouRepo.findById(vo.getSuperUnitId()));
			entity.setSubUnit(ouRepo.findById(vo.getUnitId()));

		} catch (Exception e) {
			log.error("toYearsOfHierarchy got a pesky exception: "+ e + e.getCause());
		} finally {
			return entity;
		}
	}
 */
    
}