package se.uu.ebc.bemanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import se.uu.ebc.bemanning.vo.OrganisationUnitVO;
import se.uu.ebc.bemanning.vo.YoHVO;
import se.uu.ebc.bemanning.entity.OrganisationUnit;
import se.uu.ebc.bemanning.entity.YearsOfHierarchy;

import se.uu.ebc.bemanning.repo.OrganisationUnitRepo;
import se.uu.ebc.bemanning.repo.YearsOfHierarchyRepo;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;

@Service
public class OrganisationUnitService {

    private static Logger logger = Logger.getLogger(OrganisationUnitService.class.getName());

	@Autowired
	OrganisationUnitRepo ouRepo;

	/* Organisation Units */
	
	public List<OrganisationUnitVO> getAllOrganisationUnits() throws Exception {
		List<OrganisationUnitVO> ouVO = new ArrayList<OrganisationUnitVO>();
		try {	
			for (OrganisationUnit ou : ouRepo.findAll()) {
 				ouVO.add(new OrganisationUnitVO(ou));
 			}
         	return ouVO;        	        
        } catch (Exception e) {

			return null;
			
        }
    }
    
    public OrganisationUnitVO saveOrganisationUnit(OrganisationUnitVO ouVO) throws Exception {
    	OrganisationUnit ou = ouVO.getId() == null ? toOrganisationUnit(ouVO) : toOrganisationUnit(ouRepo.findById(ouVO.getId()), ouVO);
    	ouRepo.save(ou);
		return new OrganisationUnitVO(ou);
    
    }


    public synchronized void deleteOrganisationUnit(Long cID) throws Exception {
		OrganisationUnit ou = ouRepo.findById(cID);
		ouRepo.delete(ou);
    }

   	
 
	private OrganisationUnit toOrganisationUnit (OrganisationUnitVO ouVO) throws Exception {
 		return toOrganisationUnit (new OrganisationUnit(), ouVO);
   	}

	private OrganisationUnit toOrganisationUnit (OrganisationUnit ou, OrganisationUnitVO ouVO) throws Exception {


		try {

			ou.setId(ouVO.getId());
			ou.setSvName(ouVO.getSvName());
			ou.setEnName(ouVO.getEnName());
			ou.setUnitKind(ouVO.getUnitKind());
			ou.setAbbreviation(ouVO.getAbbreviation());

			ou.setInSystem(ouVO.getInSystem());
			ou.setLegacyUnit(ouVO.getLegacyUnit());
			ou.setCourseEconomyHolder(ouVO.getCourseEconomyHolder());


		} catch (Exception e) {
			logger.error("toOrganisationUnit got a pesky exception: "+ e + e.getCause());
		} finally {
			return ou;
		}
	}
 
	@Autowired
	YearsOfHierarchyRepo yohRepo;

    
    /* Years of Hierarchy */
    
	public List<YoHVO> getAllYearsOfHierarchy() throws Exception {
		List<YoHVO> yohVO = new ArrayList<YoHVO>();
		try {	
			for (YearsOfHierarchy yoh : yohRepo.findAll()) {
 				yohVO.add(new YoHVO(yoh));
 			}
         	return yohVO;        	        
        } catch (Exception e) {

			return null;
			
        }
    }
    
    public YoHVO saveYearsOfHierarchy(YoHVO yohVO) throws Exception {
    	YearsOfHierarchy yoh = yohVO.getId() == null ? toYearsOfHierarchy(yohVO) : toYearsOfHierarchy(yohRepo.findById(yohVO.getId()), yohVO);
    	yohRepo.save(yoh);
		return new YoHVO(yoh);
    
    }


    public synchronized void deleteYearsOfHierarchy(Long id) throws Exception {
		YearsOfHierarchy yoh = yohRepo.findById(id);
		yohRepo.delete(yoh);
    }

   	
 
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
			logger.error("toYearsOfHierarchy got a pesky exception: "+ e + e.getCause());
		} finally {
			return entity;
		}
	}
    
}